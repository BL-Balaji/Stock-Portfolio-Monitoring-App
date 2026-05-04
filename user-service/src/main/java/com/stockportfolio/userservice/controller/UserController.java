package com.stockportfolio.userservice.controller;

import com.stockportfolio.userservice.dto.UserResponse;
import com.stockportfolio.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User management controller for profile and admin operations.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User profile and admin operations — JWT required")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user by ID", description = "Retrieve a user profile by their ID. Accessible by USER and ADMIN roles.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized — JWT token missing or invalid", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Get user by username", description = "Retrieve a user profile by username.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping("/profile/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserByUsername(
            @Parameter(description = "Username", example = "john_doe") @PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @Operation(summary = "Get all users (ADMIN only)", description = "Returns a list of all registered users. Requires ADMIN role.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of users returned",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Delete user (ADMIN only)", description = "Permanently deletes a user account. Requires ADMIN role.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required", content = @Content)
    })
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID to delete", example = "1") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
