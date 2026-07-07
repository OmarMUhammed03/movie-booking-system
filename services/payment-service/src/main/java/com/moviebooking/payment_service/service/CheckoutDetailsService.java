package com.moviebooking.payment_service.service;

import com.moviebooking.payment_service.dto.CheckoutSessionDetails;

import java.util.List;
import java.util.UUID;

public interface CheckoutDetailsService {

    CheckoutSessionDetails resolve(UUID showId, List<UUID> ticketIds);
}
