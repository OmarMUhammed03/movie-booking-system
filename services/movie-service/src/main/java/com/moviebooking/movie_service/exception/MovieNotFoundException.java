package com.moviebooking.movie_service.exception;

import java.util.UUID;

public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(UUID id) {
        super("Movie not found with id: " + id);
    }
}

