package com.stockportfolio.userservice.controller;

import com.stockportfolio.userservice.dto.AuthResponse;
import com.stockportfolio.userservice.dto.LoginRequest;
import com.stockportfolio.userservice.dto.RegisterRequest;
import com.stockportfolio.userservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller for register and login endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration and login — no token required")
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with USER role. Returns a JWT token on success."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User registered successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation error — check request body",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = "{\"status\":400,\"message\":\"Validation failed\",\"errors\":{\"email\":\"Invalid email format\"}}"))),
        @ApiResponse(responseCode = "409", description = "Username or email already exists",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = "{\"status\":409,\"message\":\"User already exists\"}")))
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Login and get JWT token",
        description = "Authenticates user credentials and returns a JWT Bearer token. " +
                      "Use this token in the `Authorization: Bearer <token>` header for all protected endpoints."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful — JWT token returned",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid username or password",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(value = "{\"status\":401,\"message\":\"Invalid credentials\"}"))),
        @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
