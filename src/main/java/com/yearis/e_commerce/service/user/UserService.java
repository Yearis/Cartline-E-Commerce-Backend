package com.yearis.e_commerce.service.user;

import com.yearis.e_commerce.payload.auth.EmailVerificationRequest;
import com.yearis.e_commerce.payload.auth.JwtAuthResponse;
import com.yearis.e_commerce.payload.user.*;

public interface UserService {

    UserResponse getUserProfile();

    // this will be to get user's order history

    // this will be a partial update method meaning only updating the fields we want
    UserResponse updateUserProfile(UserUpdateRequest request);

    String changePassword(PasswordChangeRequest request);

    String updateEmail(EmailUpdateRequest request);

    JwtAuthResponse verifyUpdatedEmail(EmailVerificationRequest request);

    String deleteUser(UserDeleteRequest request);
}
