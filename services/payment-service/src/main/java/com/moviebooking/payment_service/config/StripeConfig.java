package com.moviebooking.payment_service.config;

import com.stripe.Stripe;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StripeProperties.class)
public class StripeConfig {

    public StripeConfig(StripeProperties stripeProperties) {
        Stripe.apiKey = stripeProperties.getApiKey();
    }
}
