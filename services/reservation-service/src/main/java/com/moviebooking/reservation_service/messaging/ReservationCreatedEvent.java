package com.moviebooking.reservation_service.messaging;

import com.moviebooking.shared.event.ReservationSagaEvent;

public record ReservationCreatedEvent(ReservationSagaEvent sagaEvent) {
}
