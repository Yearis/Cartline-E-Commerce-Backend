package com.yearis.e_commerce.service.user;

import com.yearis.e_commerce.payload.user.PasswordChangeRequest;
import com.yearis.e_commerce.payload.user.UserDeleteRequest;
import com.yearis.e_commerce.payload.user.UserResponse;
import com.yearis.e_commerce.payload.user.UserUpdateRequest;

public interface UserService {

    UserResponse getUserProfile();

    // this will be to get user's order history

    // this will be a partial update method meaning only updating the fields we want
    UserResponse updateUserProfile(UserUpdateRequest request);

    String changePassword(PasswordChangeRequest request);

    String deleteUser(UserDeleteRequest request);
}
