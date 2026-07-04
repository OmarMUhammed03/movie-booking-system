package com.moviebooking.payment_service.service;

import com.stripe.model.checkout.Session;

import java.util.Map;

public interface StripeService {

    Session createCheckoutSession(long amountCents, String currency, Map<String, String> metadata);

}

