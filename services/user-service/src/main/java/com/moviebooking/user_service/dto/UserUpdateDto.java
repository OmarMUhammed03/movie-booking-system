package com.moviebooking.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User Update Data Transfer Object")
public class UserUpdateDto {
    @NotBlank(message = "First name is mandatory")
    @Schema(description = "First name of the user", example = "John")
    private String firstName;
    
    @NotBlank(message = "Last name is mandatory")
    @Schema(description = "Last name of the user", example = "Doe")
    private String lastName;
    
    @Schema(description = "Phone number of the user", example = "+1234567890")
    private String phone;
}
