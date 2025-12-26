package com.yearis.e_commerce.controller.user;

import com.yearis.e_commerce.payload.user.PasswordChangeRequest;
import com.yearis.e_commerce.payload.user.UserDeleteRequest;
import com.yearis.e_commerce.payload.user.UserResponse;
import com.yearis.e_commerce.payload.user.UserUpdateRequest;
import com.yearis.e_commerce.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Rest API Endpoints", description = "Operations related to user")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
@PreAuthorize("isAuthenticated()")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Update user's info/profile", description = "Update current user's info")
    @PatchMapping("/update-profile")
    public ResponseEntity<UserResponse> updateUserProfile(
            @Parameter(description = "payload for updating info") @RequestBody UserUpdateRequest request) {

        UserResponse response = userService.updateUserProfile(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update user's password", description = "Update current user's password")
    @PatchMapping("/update-pw")
    public ResponseEntity<String> changePassword(
            @Parameter(description = "payload for updating password") @RequestBody PasswordChangeRequest request) {

        String response = userService.changePassword(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Delete user's account", description = "Delete current user's account")
    @PostMapping("/delete-account")
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "payload for deleting account") @RequestBody UserDeleteRequest request) {

        String response = userService.deleteUser(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
