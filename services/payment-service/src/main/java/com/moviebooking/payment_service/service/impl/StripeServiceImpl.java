package com.moviebooking.payment_service.service.impl;

import com.moviebooking.payment_service.config.StripeProperties;
import com.moviebooking.payment_service.dto.StripeCheckoutLineItem;
import com.moviebooking.payment_service.exception.PaymentProcessingException;
import com.moviebooking.payment_service.exception.StripeWebhookException;
import com.moviebooking.payment_service.service.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {

    private final StripeProperties stripeProperties;

    @Override
    public Session createCheckoutSession(StripeCheckoutLineItem lineItem, Map<String, String> metadata) {
        SessionCreateParams.LineItem.PriceData.ProductData.Builder productBuilder =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(lineItem.productName())
                        .setDescription(lineItem.description());

        if (StringUtils.hasText(lineItem.posterUrl()) && lineItem.posterUrl().startsWith("https://")) {
            productBuilder.addImage(lineItem.posterUrl());
        }

        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(stripeProperties.getSuccessUrl())
                .setCancelUrl(stripeProperties.getCancelUrl())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(lineItem.currency())
                                                .setUnitAmount(lineItem.amountCents())
                                                .setProductData(productBuilder.build())
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

    @Override
    public Event constructWebhookEvent(String payload, String signatureHeader) {
        try {
            return Webhook.constructEvent(payload, signatureHeader, stripeProperties.getWebhookSecret());
        } catch (SignatureVerificationException ex) {
            throw new StripeWebhookException("Invalid Stripe webhook signature", ex);
        }
    }
}
