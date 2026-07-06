package com.moviebooking.ai_service.dto.response;

import java.util.UUID;

public record SimilarMovieResponse(UUID movieId, String title, Double score) {
}
