package com.stockportfolio.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT authentication response returned after login or registration")
public class AuthResponse {

    @Schema(description = "JWT Bearer token — use in Authorization header", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String token;

    @Schema(description = "Token type", example = "Bearer")
    private String tokenType;

    @Schema(description = "Username", example = "john_doe")
    private String username;

    @Schema(description = "Email address", example = "john@example.com")
    private String email;

    @Schema(description = "User role", example = "ROLE_USER")
    private String role;

    @Schema(description = "Token expiry in milliseconds", example = "86400000")
    private long expiresIn;
}
