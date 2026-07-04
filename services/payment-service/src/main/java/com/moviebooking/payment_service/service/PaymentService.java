package com.moviebooking.payment_service.service;

import com.moviebooking.payment_service.dto.request.CreateCheckoutSessionRequest;
import com.moviebooking.payment_service.dto.response.CheckoutSessionResponse;

public interface PaymentService {

    CheckoutSessionResponse createCheckoutSession(CreateCheckoutSessionRequest request);
}
