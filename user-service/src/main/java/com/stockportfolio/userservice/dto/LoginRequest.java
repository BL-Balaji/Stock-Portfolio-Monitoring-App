package com.stockportfolio.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request body for user login")
public class LoginRequest {

    @Schema(description = "Username", example = "john_doe")
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(description = "Password", example = "secret123")
    @NotBlank(message = "Password is required")
    private String password;
}
