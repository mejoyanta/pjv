package com.tax.vat.controller;

import com.tax.vat.dto.request.LoginRequest;
import com.tax.vat.dto.response.ApiResponse;
import com.tax.vat.dto.response.LoginResponse;
import com.tax.vat.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user login and logout")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "User Login", description = "Authenticate using username/email and password")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.ok("Login successful!", response);
    }

    @Operation(summary = "User Logout", description = "Logout user and mark offline")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestParam(required = false) Long userId) {
        authService.logout(userId);
        return ApiResponse.ok("Logged out successfully!");
    }
}
