package com.moviebooking.payment_service.dto.response;

import com.moviebooking.payment_service.model.PaymentStatus;

import java.util.UUID;

public record CheckoutSessionResponse(
        UUID id,
        UUID reservationId,
        String stripeSessionId,
        String checkoutUrl,
        Long amountCents,
        String currency,
        PaymentStatus status
) {
}
