package com.moviebooking.apigateway.filter;

import com.moviebooking.apigateway.constants.GatewayConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
public class GlobalFilters implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return correlationIdFilter().filter(exchange, ex ->
            requestLoggingFilter().filter(ex, ex2 ->
                responseTimingFilter().filter(ex2, ex3 ->
                    requestResponseAuditFilter().filter(ex3, chain)
                )
            )
        );
    }

    public GatewayFilter correlationIdFilter() {
        return (exchange, chain) -> {
            String correlationId = exchange.getRequest().getHeaders().getFirst(GatewayConstants.HEADER_CORRELATION_ID);
            
            if (correlationId == null) {
                correlationId = UUID.randomUUID().toString();
            }
            
            final String finalCorrelationId = correlationId;
            ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder.headers(headers -> headers.add(GatewayConstants.HEADER_CORRELATION_ID, finalCorrelationId)))
                .build();
            
            return chain.filter(mutatedExchange);
        };
    }

    public GatewayFilter requestLoggingFilter() {
        return (exchange, chain) -> {
            ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder.headers(headers -> {
                    headers.add(GatewayConstants.HEADER_X_REQUEST_START_TIME, String.valueOf(System.currentTimeMillis()));
                    headers.add(GatewayConstants.HEADER_X_REQUEST_METHOD, exchange.getRequest().getMethod().name());
                    headers.add(GatewayConstants.HEADER_X_REQUEST_PATH, exchange.getRequest().getPath().value());
                }))
                .build();
            
            return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
                long startTime = Long.parseLong(
                    mutatedExchange.getRequest().getHeaders().getFirst(GatewayConstants.HEADER_X_REQUEST_START_TIME)
                );
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                
                String logMessage = String.format(
                    "Request: %s %s - Duration: %dms - Status: %d",
                    mutatedExchange.getRequest().getMethod(),
                    mutatedExchange.getRequest().getPath(),
                    duration,
                    mutatedExchange.getResponse().getStatusCode()
                );
                
                log.info(logMessage);
            }));
        };
    }

    public GatewayFilter responseTimingFilter() {
        return (exchange, chain) -> {
            Instant startTime = Instant.now();
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                Instant endTime = Instant.now();
                long durationMs = java.time.Duration.between(startTime, endTime).toMillis();
                
                exchange.getResponse().getHeaders().add(GatewayConstants.HEADER_X_RESPONSE_TIME_MS, String.valueOf(durationMs));
                
                String timingLog = String.format(
                    "Response Time: %dms for %s %s",
                    durationMs,
                    exchange.getRequest().getMethod(),
                    exchange.getRequest().getPath()
                );
                
                log.info(timingLog);
            }));
        };
    }

    public GatewayFilter requestResponseAuditFilter() {
        return (exchange, chain) -> {
            ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder.headers(headers -> {
                    headers.add(GatewayConstants.HEADER_X_REQUEST_AUDIT_TIMESTAMP, Instant.now().toString());
                    headers.add(GatewayConstants.HEADER_X_REQUEST_USER_AGENT, exchange.getRequest().getHeaders().getFirst(HttpHeaders.USER_AGENT));
                    headers.add(GatewayConstants.HEADER_X_REQUEST_FORWARDED_FOR, exchange.getRequest().getHeaders().getFirst(GatewayConstants.HEADER_X_REQUEST_FORWARDED_FOR));
                }))
                .build();
            
            return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
                exchange.getResponse().getHeaders().add(GatewayConstants.HEADER_X_RESPONSE_AUDIT_TIMESTAMP, Instant.now().toString());
                exchange.getResponse().getHeaders().add(GatewayConstants.HEADER_X_RESPONSE_STATUS, String.valueOf(exchange.getResponse().getStatusCode()));
            }));
        };
    }
}
