package com.moviebooking.payment_service.service;

import com.moviebooking.payment_service.dto.StripeCheckoutLineItem;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;

import java.util.Map;

public interface StripeService {

    Session createCheckoutSession(StripeCheckoutLineItem lineItem, Map<String, String> metadata);

    Event constructWebhookEvent(String payload, String signatureHeader);
}
