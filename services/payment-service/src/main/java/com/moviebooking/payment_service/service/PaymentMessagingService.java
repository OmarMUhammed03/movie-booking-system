package com.moviebooking.payment_service.service;

import com.moviebooking.payment_service.model.Payment;

public interface PaymentMessagingService {

    void publishPaymentSucceeded(Payment payment);

    void publishPaymentFailed(Payment payment, String reason);
}
