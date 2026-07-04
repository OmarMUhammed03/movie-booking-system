package com.moviebooking.payment_service.controller;

import com.moviebooking.payment_service.dto.request.CreateCheckoutSessionRequest;
import com.moviebooking.payment_service.dto.response.CheckoutSessionResponse;
import com.moviebooking.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutSessionResponse> createCheckoutSession(
            @Valid @RequestBody CreateCheckoutSessionRequest request) {
        CheckoutSessionResponse response = paymentService.createCheckoutSession(request);
        URI location = URI.create("/api/payments/checkout/" + response.id());
        return ResponseEntity.created(location).body(response);
    }
}
