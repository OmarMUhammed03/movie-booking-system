package com.moviebooking.apigateway.config;

import com.moviebooking.apigateway.constants.GatewayConstants;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

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
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of(
            GatewayConstants.HEADER_CORRELATION_ID,
            GatewayConstants.HEADER_X_RESPONSE_TIME_MS
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return new CorsWebFilter(source);
    }
}
