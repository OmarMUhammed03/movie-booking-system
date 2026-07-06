package com.moviebooking.apigateway.config;

import com.moviebooking.apigateway.constants.GatewayConstants;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(
            GatewayConstants.RATE_LIMIT_REPLENISH_RATE,
            GatewayConstants.RATE_LIMIT_LIMIT
        );
    }
}