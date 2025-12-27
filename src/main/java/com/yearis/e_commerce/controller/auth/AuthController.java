package com.yearis.e_commerce.controller.auth;

import com.yearis.e_commerce.payload.auth.JwtAuthResponse;
import com.yearis.e_commerce.payload.auth.LoginRequest;
import com.yearis.e_commerce.payload.auth.RegisterRequest;
import com.yearis.e_commerce.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth Rest API Endpoints", description = "Operations related to authentication")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a user", description = "Register/create a new user")
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Parameter(description = "payload for registering") @RequestBody RegisterRequest request) {

        String response = authService.register(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Login a user", description = "Login an existing user")
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(
            @Parameter(description = "payload for logging in") @RequestBody LoginRequest request) {

        JwtAuthResponse response = authService.login(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
