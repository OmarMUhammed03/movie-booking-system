package com.moviebooking.ai_service.dto;

import java.util.List;

/**
 * Minimal projection of Spring Data's Page JSON returned by movie-service.
 */
public record PageDto<T>(
        List<T> content, int totalPages, long totalElements, int number, boolean last) {
}
