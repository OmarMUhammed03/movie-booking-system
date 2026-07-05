# API Gateway Module

This module implements the API Gateway for the Movie Booking System, serving as the single entry point for all client requests while integrating seamlessly with the existing microservices architecture.

## Overview

The API Gateway provides:
- Centralized authentication and authorization using JWT tokens
- Dynamic service discovery through Eureka
- Rate limiting to protect services from abuse
- Request/response logging and monitoring
- Circuit breakers for resilience
- CORS configuration
- Load balancing across service instances

## Key Features

### 1. Spring Cloud Gateway Integration
- Routes requests to appropriate microservices using Eureka service discovery
- Supports dynamic routing with `lb://SERVICE-NAME` pattern
- Automatic service instance discovery and load balancing

### 2. JWT Authentication
- Integrates with the existing shared-security module
- Validates JWT tokens before forwarding requests
- Preserves authentication information for downstream services
- Defense in depth with backend service validation

### 3. Rate Limiting
- IP-based rate limiting for public endpoints (login, registration)
- User-based rate limiting for authenticated endpoints
- Configurable limits via Redis
- KeyResolver implementations for different rate limiting strategies

### 4. Global Gateway Filters
- Request logging with headers, query parameters, and payload
- Correlation ID generation and propagation (`X-Correlation-ID`)
- Request/response timing and latency logging
- Optional request/response auditing

### 5. CORS Configuration
- Centralized CORS configuration in the gateway
- Configurable allowed origins, methods, and headers
- Support for credentials and max-age

### 6. Resilience Improvements
- Circuit breakers using Resilience4j
- Gateway timeouts and retry mechanisms
- Fallback responses for unavailable services

## Project Structure

```
services/api-gateway/
├── pom.xml
├── src/main/java/com/moviebooking/apigateway/
│   ├── ApiGatewayApplication.java
│   ├── config/
│   │   ├── GatewaySecurityConfig.java
│   │   ├── RateLimitingConfig.java
│   │   └── GatewayConfig.java
│   ├── filter/
│   │   ├── JwtAuthenticationFilter.java
│   │   └── GlobalFilters.java
│   └── exceptions/
│       ├── InvalidTokenException.java
│       ├── UnauthorizedException.java
│       └── ServiceUnavailableException.java
├── src/main/resources/
│   └── application.yml
└── README.md
```

## Dependencies

### Maven Dependencies
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-loadbalancer</artifactId>
    </dependency>
    <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
        </dependency>
    <dependency>
        <groupId>com.moviebooking</groupId>
        <artifactId>shared-security</artifactId>
        <version>${project.version}</version>
    </dependency>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-spring-boot3</artifactId>
        <version>2.2.0</version>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

## Configuration

### application.yml

The gateway configuration includes:

1. **Server Configuration**
   - Port: 8080
   - Context path: /

2. **Spring Cloud Gateway Routes**
   - User Service: `/api/users/**` → `lb://user-service`
   - Movie Service: `/api/movies/**` → `lb://movie-service`
   - Reservation Service: `/api/reservations/**` → `lb://reservation-service`
   - Payment Service: `/api/payments/**` → `lb://payment-service`

3. **Eureka Configuration**
   - Service discovery enabled
   - Health checks enabled
   - Lease renewal and expiration settings

4. **Redis Configuration**
   - Rate limiting storage
   - Connection pool settings

5. **Security Configuration**
   - JWT token validation
   - OAuth2 resource server configuration

6. **Resilience4j Configuration**
   - Circuit breaker settings
   - Rate limiting configurations

7. **Rate Limiting**
   - IP-based limits for login/registration
   - User-based limits for authenticated endpoints

8. **CORS Configuration**
   - Allowed origins, methods, and headers
   - Credentials and max-age settings

## Security

### JWT Authentication
The gateway uses the existing shared-security module for JWT authentication:

1. **Token Validation**
   - Validates JWT tokens from the `Authorization` header
   - Extracts user information and authorities
   - Adds user info to request headers for downstream services

2. **Authentication Flow**
   - Clients send JWT tokens in the `Authorization: Bearer <token>` header
   - Gateway validates the token using the shared-security module
   - If valid, adds user information to request headers
   - Forwards request to the appropriate service

3. **Authorization**
   - Services continue to validate JWTs independently (defense in depth)
   - Gateway rejects unauthorized requests before forwarding

## Rate Limiting

### Configuration
Rate limiting is configured in `application.yml` using Spring Cloud Gateway's `RequestRateLimiter` filter with Redis:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://auth-service
          predicates:
            - Path=/api/auth/**
          filters:
            - StripPrefix=1
            - name: RequestRateLimiter
              args:
                redis-rate-limiter: #{@redisRateLimiter}
                key-resolver: #{@ipKeyResolver}
```

The `RedisRateLimiter` is configured with:
- `replenishRate`: 1 request per second
- `burstCapacity`: 10 requests

### KeyResolvers
- **IP KeyResolver**: Uses client IP address for IP-based limiting (used for auth-service)
- **User KeyResolver**: Uses `X-User-Email` header for user-based limiting (used for user, reservation, payment services)
- **Path KeyResolver**: Uses request path for path-based limiting (available for future use)

## Global Filters

### Correlation ID
- Generates unique correlation ID if not present
- Propagates correlation ID to downstream services
- Enables request tracing across services

### Request/Response Logging
- Logs request details (method, path, headers, query parameters)
- Logs response details (status code, response time)
- Includes correlation ID in logs for tracing

### Request/Response Timing
- Measures request processing time
- Adds timing information to response headers
- Logs timing information for monitoring

### Request/Response Auditing
- Optional audit logging of request/response payloads
- Configurable sensitive field filtering
- Useful for security and compliance

## Docker Integration

### Docker Compose
The API Gateway is included in the docker-compose.yml file:

```yaml
services:
  eureka-server:
    image: springcloud/eureka-server:latest
    container_name: eureka-server
    ports:
      - "8761:8761"

  api-gateway:
    build: ./services/api-gateway
    container_name: api-gateway
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE: http://eureka-server:8761/eureka/
    depends_on:
      - eureka-server
      - redis
    networks:
      - movie-network

  redis:
    image: redis:7-alpine
    container_name: redis
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data
    networks:
      - movie-network
```

### Docker Build
The API Gateway can be built using Maven:

```bash
cd services/api-gateway
mvn clean package
```

## Deployment

### Local Development
1. Ensure all microservices are running
2. Start Eureka Server
3. Start API Gateway
4. Start Redis

### Production Deployment
1. Build all services
2. Deploy to container orchestration platform (Kubernetes, Docker Swarm)
3. Configure external load balancer
4. Set up monitoring and logging

## Monitoring and Observability

### Actuator Endpoints
The gateway exposes the following actuator endpoints:
- `/actuator/health`: Health check
- `/actuator/info`: Application information
- `/actuator/metrics`: Metrics
- `/actuator/circuitbreakers`: Circuit breaker status
- `/actuator/circuitbreakerevents`: Circuit breaker events
- `/actuator/httptrace`: HTTP trace
- `/actuator/loggers`: Logger configuration

### Metrics
- Request count and response time
- Circuit breaker statistics
- Rate limiting statistics
- Error rates

### Logging
- Request/response logging
- Correlation ID tracking
- Error logging with stack traces
- Performance metrics

## Testing

### Unit Tests
- JWT authentication filter tests
- Rate limiting tests
- Global filter tests

### Integration Tests
- End-to-end API testing
- Service discovery testing
- Rate limiting integration testing

## Troubleshooting

### Common Issues

1. **Service Not Found**
   - Ensure Eureka server is running
   - Check service registration
   - Verify service names in routes

2. **Authentication Failures**
   - Check JWT token validity
   - Verify shared-security configuration
   - Check token issuer and audience

3. **Rate Limiting Issues**
   - Check Redis connectivity
   - Verify rate limiting configuration
   - Check KeyResolver implementations

4. **Performance Issues**
   - Check circuit breaker settings
   - Verify connection pool configuration
   - Monitor resource usage

## Future Enhancements

1. **Advanced Rate Limiting**
   - Sliding window algorithm
   - Token bucket algorithm
   - Custom rate limiting strategies

2. **Advanced Security**
   - OAuth2 resource server with scopes
   - API key authentication
   - Request validation

3. **Advanced Monitoring**
   - Distributed tracing
   - Custom metrics
   - Alerting integration

4. **Advanced Caching**
   - Response caching
   - Service discovery caching
   - Rate limiting caching

## Conclusion

The API Gateway provides a robust, secure, and scalable entry point for the Movie Booking System. It integrates seamlessly with the existing microservices architecture while adding valuable features like authentication, rate limiting, and monitoring. The implementation follows Spring Boot and Spring Cloud best practices, ensuring maintainability and performance.