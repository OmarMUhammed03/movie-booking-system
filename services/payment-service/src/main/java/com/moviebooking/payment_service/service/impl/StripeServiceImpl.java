package com.moviebooking.payment_service.service.impl;

import com.moviebooking.payment_service.config.StripeProperties;
import com.moviebooking.payment_service.exception.PaymentProcessingException;
import com.moviebooking.payment_service.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {

    private final StripeProperties stripeProperties;

    @Override
    public Session createCheckoutSession(long amountCents, String currency, Map<String, String> metadata) {
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(stripeProperties.getSuccessUrl())
                .setCancelUrl(stripeProperties.getCancelUrl())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(currency)
                                                .setUnitAmount(amountCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Movie Ticket")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                );

        metadata.forEach(paramsBuilder::putMetadata);

        try {
            return Session.create(paramsBuilder.build());
        } catch (StripeException ex) {
            throw new PaymentProcessingException("Failed to create Stripe checkout session", ex);
        }
    }
}
