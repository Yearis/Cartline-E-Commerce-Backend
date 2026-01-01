package com.yearis.e_commerce.service.auth;

import com.yearis.e_commerce.entity.Cart;
import com.yearis.e_commerce.entity.Role;
import com.yearis.e_commerce.entity.User;
import com.yearis.e_commerce.exception.*;
import com.yearis.e_commerce.payload.auth.*;
import com.yearis.e_commerce.repository.role.RoleRepository;
import com.yearis.e_commerce.repository.user.UserRepository;
import com.yearis.e_commerce.security.JwtService;
import com.yearis.e_commerce.security.RefreshTokenService;
import com.yearis.e_commerce.service.email.EmailService;
import com.yearis.e_commerce.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;

    // --- Helper ---
    private String generateOtp() {
        // this will generate a number between 100000 and 999999
        SecureRandom secureRandom = new SecureRandom();
        int code = secureRandom.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    private void checkUserNotBlocked(String email) {
        if (redisService.isBlocked(email)) {
            throw new ActionNotAllowedException("Too many failed attempts. Please try again in 15 minutes.");
        }
    }

    private User buildUser(RegisterRequest request) {

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setVerified(false);

        // now we set the userRole for user initially user is only a user,
        // but they can apply for seller which will be only approved by admin
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("Role not found!!"));

        user.setRoles(Set.of(userRole));

        // we make the cart for user that will be permanent for this user
        Cart userCart = new Cart();
        userCart.setUser(user);

        user.setCart(userCart);

        return user;
    }

    @Override
    @Transactional
    public String register(RegisterRequest request) {

        // 1st we check if the email is used by another user
        if (userRepository.existsByEmail(request.getEmail())) {

            throw new UserAlreadyExistsException("Email is already been used!\nTry another email");
        }

        checkUserNotBlocked(request.getEmail());

        // if every is okay we build the user
        User newUser = buildUser(request);

        userRepository.save(newUser);

        // now we will send the mail and all
        String otp = generateOtp();

        redisService.saveOtp(newUser.getEmail(), otp);

        emailService.sendVerificationEmail(newUser.getEmail(), otp);

        return "Verification code sent to " + newUser.getEmail();
    }

    @Override
    @Transactional
    public String verifyEmail(EmailVerificationRequest request) {

        checkUserNotBlocked(request.getEmail());

        // to verify someone 1st we will get the otp from user
        String storedOtp = redisService.getOtp(request.getEmail());

        if (storedOtp == null || !storedOtp.equals(request.getOtp())) {

            redisService.incrementAttemptsFailed(request.getEmail());

            throw new BadRequestException("Invalid or expired OTP");
        }

        redisService.clearAttempts(request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setVerified(true);
        userRepository.save(user);

        redisService.deleteOtp(request.getEmail());

        return "Email verified successfully! You can now login.";
    }

    @Override
    @Transactional
    public String forgotPassword(ForgetPasswordRequest request) {

        // we check if user even exists
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new UsernameNotFoundException("user not found");
        }

        checkUserNotBlocked(request.getEmail());

        String otp = generateOtp();
        redisService.saveOtp(request.getEmail(), otp);

        emailService.sendVerificationEmail(request.getEmail(), otp);

        return "Password reset code sent to " + request.getEmail();
    }

    @Override
    @Transactional
    public String verifyOtp(EmailVerificationRequest request) {

        checkUserNotBlocked(request.getEmail());

        String savedOtp = redisService.getOtp(request.getEmail());

        if (savedOtp == null || !savedOtp.equals(request.getOtp())) {

            redisService.incrementAttemptsFailed(request.getEmail());

            throw new BadRequestException("Invalid or expired OTP");
        }

        redisService.clearAttempts(request.getEmail());

        return "OTP Verified. You can now set your new password.";
    }

    @Override
    @Transactional
    public String resetPassword(ResetPasswordRequest request) {

        if (!request.getNewPassword().equals(request.getConfirmationNewPassword())) {
            throw new InvalidPasswordException("Password dont match");
        }

        checkUserNotBlocked(request.getEmail());

        String savedOtp = redisService.getOtp(request.getEmail());

        if (savedOtp == null || !savedOtp.equals(request.getOtp())) {

            redisService.incrementAttemptsFailed(request.getEmail());

            throw new BadRequestException("Invalid or expired OTP");
        }

        redisService.clearAttempts(request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        redisService.deleteOtp(request.getEmail());

        return "Password has been successfully reset! Please login.";
    }

    @Override
    @Transactional
    public String resendOtp(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.isVerified()) {
            throw new ActionNotAllowedException("User already verified");
        }

        checkUserNotBlocked(email);

        String otp = generateOtp();
        redisService.saveOtp(email, otp);
        emailService.sendVerificationEmail(email, otp);

        return "New verification code sent to " + email;
    }

    @Override
    @Transactional
    public JwtAuthResponse login(LoginRequest request) {

        // we authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isVerified()) {
            throw new ActionNotAllowedException("Your account is not verified. Please verify your email.");
        }

        redisService.clearAttempts(request.getEmail());

        String accessToken = jwtService.generateToken(new HashMap<>(), (UserDetails) authentication.getPrincipal());

        String refreshToken = refreshTokenService.createRefreshToken(request.getEmail());

        return new JwtAuthResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public JwtAuthResponse refreshToken(RefreshTokenRequest request) {

        // we 1st check the token
        String email = refreshTokenService.verifyRefreshToken(request.getRefreshToken());

        // now we have the email we can get thee user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // now we just generate a new token
        String accessToken = jwtService.generateToken(new HashMap<>(), (UserDetails) user);

        return new JwtAuthResponse(accessToken, request.getRefreshToken());
    }

    @Override
    @Transactional
    public String logout(RefreshTokenRequest request) {

        refreshTokenService.deleteRefreshToken(request.getRefreshToken());

        return "Logged out successfully";
    }
}
