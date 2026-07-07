package com.moviebooking.user_service.controller;

import com.moviebooking.shared.dto.PaginationRequest;
import com.moviebooking.shared.dto.PaginationResponse;
import com.moviebooking.user_service.dto.UserDto;
import com.moviebooking.user_service.dto.UserPatchDto;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Controller", description = "Endpoints for managing user profiles")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a paginated list of user profiles with optional name search")
    public ResponseEntity<PaginationResponse<UserDto>> getAllUsers(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(name = "size", defaultValue = "10") int size,
            @Parameter(description = "Search query for name (first name + last name)") @RequestParam(name = "name", defaultValue = "", required = false) String name) {
        log.info("Fetching all users with pagination: page={}, size={}, name={}", page, size, name);
        PaginationRequest paginationRequest = PaginationRequest.builder()
                .page(page)
                .size(size)
                .name(name)
                .build();
        PaginationResponse<UserDto> response = userService.getAllUsers(paginationRequest);
        log.info("Found {} users out of {} total", response.getContent().size(), response.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user profile by its unique ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "ID of the user to be retrieved") @PathVariable("id") UUID id) {
        log.info("Fetching user by id: {}", id);
        UserDto user = userService.getUserById(id);
        log.info("Found user with id: {}", id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get user by Auth User ID", description = "Retrieves a user profile by its corresponding Auth service user ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/auth/{authUserId}")
    public ResponseEntity<UserDto> getUserByAuthUserId(
            @Parameter(description = "Auth User ID of the user to be retrieved")
            @PathVariable("authUserId") UUID authUserId) {
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

    @PatchMapping("/{id}")
    @Operation(summary = "Patch user", description = "Partially updates an existing user profile")
    @ApiResponse(responseCode = "200", description = "User patched successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserDto> patchUser(
            @Parameter(description = "ID of the user to be patched") @PathVariable UUID id,
            @RequestBody UserPatchDto patchDto) {
        log.info("Patching user with id: {}", id);
        UserDto patchedUser = userService.patchUser(id, patchDto);
        log.info("Successfully patched user with id: {}", id);
        return ResponseEntity.ok(patchedUser);
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
