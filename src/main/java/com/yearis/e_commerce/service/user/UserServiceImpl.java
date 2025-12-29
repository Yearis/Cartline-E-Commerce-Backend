package com.yearis.e_commerce.service.user;

import com.yearis.e_commerce.entity.User;
import com.yearis.e_commerce.exception.InvalidPasswordException;
import com.yearis.e_commerce.payload.user.PasswordChangeRequest;
import com.yearis.e_commerce.payload.user.UserDeleteRequest;
import com.yearis.e_commerce.payload.user.UserResponse;
import com.yearis.e_commerce.payload.user.UserUpdateRequest;
import com.yearis.e_commerce.repository.user.UserRepository;
import com.yearis.e_commerce.service.seller.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SellerService sellerService;

    // get our current user
    private User currentUser() {

        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // --- Mappers ---
    private UserResponse mapToResponse(User user) {

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());

        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        response.setRoles(roles);
        return response;
    }

    @Override
    public UserResponse getUserProfile() {

        User currentUser = currentUser();

        return mapToResponse(currentUser);
    }

    @Override
    @Transactional
    public UserResponse updateUserProfile(UserUpdateRequest request) {

        // here we will only update what is sent in the request
        User currentUser = currentUser();
        boolean isUpdated = false;

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {

            if (!request.getFirstName().equals(currentUser.getFirstName())) {
                currentUser.setFirstName(request.getFirstName());
                isUpdated = true;
            }
        }

        if (request.getLastName() != null && !request.getLastName().isBlank()) {

            if (!request.getLastName().equals(currentUser.getLastName())) {
                currentUser.setLastName(request.getLastName());
                isUpdated = true;
            }
        }

        if (isUpdated) {
            User savedUser = userRepository.save(currentUser);
            return mapToResponse(savedUser);
        }

        return mapToResponse(currentUser);
    }

    @Override
    @Transactional
    public String changePassword(PasswordChangeRequest request) {

        User currentUser = currentUser();

        // now we check if newPassword and confirmedNewPassword match or not
        if (!request.getNewPassword().equals(request.getConfirmationNewPassword())) {

            throw new InvalidPasswordException("New password and Confirm New password fields should match");
        }

        // now we check if our old password is correct and our new password doesn't match our old password
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {

            throw new InvalidPasswordException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), currentUser.getPassword())) {

            throw new InvalidPasswordException("New password cannot be same as old password");
        }

        // now we set the password
        currentUser.setPassword(passwordEncoder.encode(request.getConfirmationNewPassword()));

        // now we save it
        userRepository.save(currentUser);

        return "Password updated!";
    }

    @Override
    @Transactional
    public String deleteUser(UserDeleteRequest request) {

        // we get the current user as only you are able to delete your own acc
        User currentUser = currentUser();

        if (!passwordEncoder.matches(request.getUserPassword(), currentUser.getPassword())) {

            throw new InvalidPasswordException("Invalid Password");
        }

        // if user is a seller we don't delete the account
        if (currentUser.getSeller() != null) {

            sellerService.deactivateSellerAccount();
        }

        currentUser.setFirstName("Deleted");
        currentUser.setLastName("User");

        String uniqueDeletedEmail = "deleted_" + currentUser.getId() + "@cartline.com";
        currentUser.setEmail(uniqueDeletedEmail);

        currentUser.setPassword("DELETED_ACCOUNT_" + java.util.UUID.randomUUID());
        currentUser.setDeleted(true);

        currentUser.getRoles().clear();

        // if everything's alright we delete it now
        userRepository.save(currentUser);

        SecurityContextHolder.clearContext();

        return "User Successfully Deleted!";
    }
}
