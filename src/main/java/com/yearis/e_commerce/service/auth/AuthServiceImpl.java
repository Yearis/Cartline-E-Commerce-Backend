package com.yearis.e_commerce.service.auth;

import com.yearis.e_commerce.entity.Cart;
import com.yearis.e_commerce.entity.Role;
import com.yearis.e_commerce.entity.User;
import com.yearis.e_commerce.payload.auth.JwtAuthResponse;
import com.yearis.e_commerce.payload.auth.LoginRequest;
import com.yearis.e_commerce.payload.auth.RegisterRequest;
import com.yearis.e_commerce.repository.role.RoleRepository;
import com.yearis.e_commerce.repository.user.UserRepository;
import com.yearis.e_commerce.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // --- Helper ---
    private User buildUser(RegisterRequest request) {

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // now we set the userRole for user initially user is only a user,
        // but they can apply for seller which will be only approved by admin
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role not found!!"));

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

            throw new RuntimeException("Email is already been used!\nTry another email");
        }

        // if every is okay we build the user
        User newUser = buildUser(request);

        userRepository.save(newUser);

        return "User registered!!";
    }

    @Override
    public JwtAuthResponse login(LoginRequest request) {

        // we authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
        );

        String token = jwtService.generateToken(new HashMap<>(), (UserDetails) authentication.getPrincipal());

        return new JwtAuthResponse(token);
    }
}
