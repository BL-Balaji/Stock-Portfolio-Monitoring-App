package com.stockportfolio.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request body for user registration")
public class RegisterRequest {

    @Schema(description = "Unique username", example = "john_doe", minLength = 3, maxLength = 50)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(description = "Valid email address", example = "john@example.com")
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "Password (minimum 6 characters)", example = "secret123", minLength = 6)
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
