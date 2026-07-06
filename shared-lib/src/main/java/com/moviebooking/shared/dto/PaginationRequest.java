package com.moviebooking.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Pagination Request Data Transfer Object")
public class PaginationRequest {
    
    @Min(value = 0, message = "Page number must be non-negative")
    @Schema(description = "Page number (0-indexed)", example = "0")
    private int page = 0;
    
    @Min(value = 1, message = "Page size must be at least 1")
    @Schema(description = "Number of items per page", example = "10")
    private int size = 10;
    
    @Schema(description = "Search query for name (first name + last name)", example = "John")
    private String name;
}