package com.moviebooking.apigateway.constants;

public final class GatewayConstants {

    private GatewayConstants() {
    }

    // ==================== Rate Limiting ====================
    public static final int RATE_LIMIT_LIMIT = 10;
    public static final int RATE_LIMIT_TIMEOUT = 20;
    public static final int RATE_LIMIT_REPLENISH_RATE = 1;

    // ==================== Security Headers ====================
    public static final String HEADER_X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
    public static final String HEADER_X_FRAME_OPTIONS = "X-Frame-Options";
    public static final String HEADER_X_XSS_PROTECTION = "X-XSS-Protection";
    public static final String HEADER_STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";

    public static final String SECURITY_HEADER_VALUE_X_CONTENT_TYPE_OPTIONS = "nosniff";
    public static final String SECURITY_HEADER_VALUE_X_FRAME_OPTIONS = "DENY";
    public static final String SECURITY_HEADER_VALUE_X_XSS_PROTECTION = "1; mode=block";
    public static final String SECURITY_HEADER_VALUE_STRICT_TRANSPORT_SECURITY = "max-age=31536000; includeSubDomains";

    // ==================== CORS Headers ====================
    public static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    public static final String HEADER_ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    public static final String HEADER_ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

    public static final String CORS_ALLOWED_ORIGIN = "*";
    public static final String CORS_ALLOWED_METHODS = "GET,POST,PUT,DELETE,OPTIONS";
    public static final String CORS_ALLOWED_HEADERS = "*";
    public static final String CORS_ALLOW_CREDENTIALS = "false";
    public static final String CORS_EXPOSE_HEADERS = "*";
    public static final String CORS_MAX_AGE = "3600";

    // ==================== Rate Limiting Key Resolvers ====================
    public static final String HEADER_X_USER_EMAIL = "X-User-Email";
    public static final String KEY_ANONYMOUS = "anonymous";

    // ==================== Correlation ID ====================
    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";

    // ==================== Request Logging ====================
    public static final String HEADER_X_REQUEST_START_TIME = "X-Request-Start-Time";
    public static final String HEADER_X_REQUEST_METHOD = "X-Request-Method";
    public static final String HEADER_X_REQUEST_PATH = "X-Request-Path";

    // ==================== Response Timing ====================
    public static final String HEADER_X_RESPONSE_TIME_MS = "X-Response-Time-Ms";

    // ==================== Audit Headers ====================
    public static final String HEADER_X_REQUEST_AUDIT_TIMESTAMP = "X-Request-Audit-Timestamp";
    public static final String HEADER_X_REQUEST_USER_AGENT = "X-Request-User-Agent";
    public static final String HEADER_X_REQUEST_FORWARDED_FOR = "X-Forwarded-For";
    public static final String HEADER_X_RESPONSE_AUDIT_TIMESTAMP = "X-Response-Audit-Timestamp";
    public static final String HEADER_X_RESPONSE_STATUS = "X-Response-Status";

    // ==================== JWT Authentication ====================
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_X_USER_ROLES = "X-User-Roles";
}
