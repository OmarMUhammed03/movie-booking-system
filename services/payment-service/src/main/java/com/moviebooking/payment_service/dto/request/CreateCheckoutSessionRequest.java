package com.moviebooking.payment_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCheckoutSessionRequest(
        @NotNull(message = "reservationId is required")
        UUID reservationId,

        @NotNull(message = "amountCents is required")
        @Min(value = 50, message = "amountCents must be at least 50")
        Long amountCents,

        @NotBlank(message = "currency is required")
        @Size(min = 3, max = 3, message = "currency must be a 3-letter ISO code")
        @Pattern(regexp = "[a-z]{3}", message = "currency must be lowercase ISO 4217 code")
        String currency
) {
}
