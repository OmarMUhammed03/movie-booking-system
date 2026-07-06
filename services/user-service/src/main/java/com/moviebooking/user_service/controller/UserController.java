package com.moviebooking.user_service.controller;

import com.moviebooking.user_service.dto.UserDto;
import com.moviebooking.user_service.dto.UserUpdateDto;
import com.moviebooking.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Controller", description = "Endpoints for managing user profiles")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a list of all user profiles")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Fetching all users");
        List<UserDto> users = userService.getAllUsers();
        log.info("Found {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user profile by its unique ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "ID of the user to be retrieved") @PathVariable UUID id) {
        log.info("Fetching user by id: {}", id);
        UserDto user = userService.getUserById(id);
        log.info("Found user with id: {}", id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/auth/{authUserId}")
    @Operation(summary = "Get user by Auth User ID", description = "Retrieves a user profile by its corresponding Auth service user ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserDto> getUserByAuthUserId(
            @Parameter(description = "Auth User ID of the user to be retrieved") @PathVariable UUID authUserId) {
        log.info("Fetching user by authUserId: {}", authUserId);
        UserDto user = userService.getUserByAuthUserId(authUserId);
        log.info("Found user with authUserId: {}", authUserId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates an existing user profile")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "ID of the user to be updated") @PathVariable UUID id,
            @Valid @RequestBody UserUpdateDto updateDto) {
        log.info("Updating user with id: {}", id);
        UserDto updatedUser = userService.updateUser(id, updateDto);
        log.info("Successfully updated user with id: {}", id);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user profile by its ID")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to be deleted") @PathVariable UUID id) {
        log.info("Deleting user with id: {}", id);
        userService.deleteUser(id);
        log.info("Successfully deleted user with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
