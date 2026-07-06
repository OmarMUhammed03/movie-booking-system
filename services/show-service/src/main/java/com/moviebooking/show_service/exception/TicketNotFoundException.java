package com.moviebooking.show_service.exception;

import java.util.UUID;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(UUID id) {
        super("Ticket not found with id: " + id);
    }
}
