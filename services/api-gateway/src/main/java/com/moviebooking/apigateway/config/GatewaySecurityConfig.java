package com.moviebooking.apigateway.config;

import com.moviebooking.apigateway.constants.GatewayConstants;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class GatewaySecurityConfig {

    @Bean
    public GatewayFilter securityHeadersFilter() {
        return (exchange, chain) -> {
            ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder.headers(headers -> {
                    headers.add(GatewayConstants.HEADER_X_CONTENT_TYPE_OPTIONS, GatewayConstants.SECURITY_HEADER_VALUE_X_CONTENT_TYPE_OPTIONS);
                    headers.add(GatewayConstants.HEADER_X_FRAME_OPTIONS, GatewayConstants.SECURITY_HEADER_VALUE_X_FRAME_OPTIONS);
                    headers.add(GatewayConstants.HEADER_X_XSS_PROTECTION, GatewayConstants.SECURITY_HEADER_VALUE_X_XSS_PROTECTION);
                    headers.add(GatewayConstants.HEADER_STRICT_TRANSPORT_SECURITY, GatewayConstants.SECURITY_HEADER_VALUE_STRICT_TRANSPORT_SECURITY);
                }))
                .build();
            return chain.filter(mutatedExchange);
        };
    }

    @Bean
    public GatewayFilter corsFilter() {
        return (exchange, chain) -> {
            ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder.headers(headers -> {
                    headers.add(GatewayConstants.HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, GatewayConstants.CORS_ALLOWED_ORIGIN);
                    headers.add(GatewayConstants.HEADER_ACCESS_CONTROL_ALLOW_METHODS, GatewayConstants.CORS_ALLOWED_METHODS);
                    headers.add(GatewayConstants.HEADER_ACCESS_CONTROL_ALLOW_HEADERS, GatewayConstants.CORS_ALLOWED_HEADERS);
                    headers.add(GatewayConstants.HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS, GatewayConstants.CORS_ALLOW_CREDENTIALS);
                    headers.add(GatewayConstants.HEADER_ACCESS_CONTROL_EXPOSE_HEADERS, GatewayConstants.CORS_EXPOSE_HEADERS);
                    headers.add(GatewayConstants.HEADER_ACCESS_CONTROL_MAX_AGE, GatewayConstants.CORS_MAX_AGE);
                }))
                .build();
            return chain.filter(mutatedExchange);
        };
    }
}
