package com.moviebooking.payment_service.repository;

import com.moviebooking.payment_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Payment findByReservationId(UUID reservationId);

    Payment findByStripeSessionId(String stripeSessionId);
}
