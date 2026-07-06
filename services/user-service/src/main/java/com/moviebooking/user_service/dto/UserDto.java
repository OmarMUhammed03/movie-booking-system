package com.moviebooking.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User Data Transfer Object")
public class UserDto {
    @Schema(description = "Unique identifier of the user", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    
    @Schema(description = "ID from the Auth service", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID authUserId;
    
    @Schema(description = "First name of the user", example = "John")
    private String firstName;
    
    @Schema(description = "Last name of the user", example = "Doe")
    private String lastName;
    
    @Schema(description = "Phone number of the user", example = "+1234567890")
    private String phone;
}
