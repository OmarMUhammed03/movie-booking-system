package com.moviebooking.show_service.exception;

import java.util.UUID;

public class HallNotFoundException extends RuntimeException {
    public HallNotFoundException(UUID id) {
        super("Hall not found with id: " + id);
    }
}
