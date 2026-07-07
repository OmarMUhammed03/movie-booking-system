package com.moviebooking.payment_service.dto;

public record StripeCheckoutLineItem(
        long amountCents,
        String currency,
        String productName,
        String description,
        String posterUrl
) {
}
