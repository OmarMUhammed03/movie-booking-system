package com.moviebooking.apigateway.filter;

import com.moviebooking.apigateway.exceptions.InvalidTokenException;
import com.moviebooking.apigateway.exceptions.ServiceUnavailableException;
import com.moviebooking.apigateway.exceptions.UnauthorizedException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class GatewayExceptionHandlerFilter implements GatewayFilter, Ordered {

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE + 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
            .onErrorResume(UnauthorizedException.class, e -> handleException(exchange, HttpStatus.UNAUTHORIZED, "Unauthorized", e.getMessage()))
            .onErrorResume(InvalidTokenException.class, e -> handleException(exchange, HttpStatus.UNAUTHORIZED, "Invalid Token", e.getMessage()))
            .onErrorResume(ServiceUnavailableException.class, e -> handleException(exchange, HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", e.getMessage()));
    }

    private Mono<Void> handleException(ServerWebExchange exchange, HttpStatus status, String error, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        String body = String.format(
            "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
            java.time.Instant.now().toString(),
            status.value(),
            error,
            message != null ? message.replace("\"", "\\\"") : "No additional information",
            exchange.getRequest().getPath().value()
        );
        
        return exchange.getResponse().writeWith(
            Mono.just(exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8)))
        );
    }
}
