package com.moviebooking.show_service.exception;

import java.util.UUID;

public class ShowNotFoundException extends RuntimeException {
    public ShowNotFoundException(UUID id) {
        super("Show not found with id: " + id);
    }
}
