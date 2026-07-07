package com.moviebooking.payment_service.dto;

public record CheckoutSessionDetails(
        String productName,
        String description,
        String posterUrl
) {
}
