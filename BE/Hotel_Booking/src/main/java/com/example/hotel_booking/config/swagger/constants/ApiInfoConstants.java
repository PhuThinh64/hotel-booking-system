package com.example.hotel_booking.config.swagger.constants;

public final class ApiInfoConstants {

    private ApiInfoConstants() {
        // Prevent instantiation
    }

    /* ============================================================
     * API INFORMATION
     * ============================================================ */
    public static final String TITLE = "Hotel Booking Management REST API";
    public static final String VERSION = "1.0.0";

    public static final String API_DESCRIPTION = """
        RESTful API documentation for the Hotel Booking Management System.

        This API provides endpoints for:

        • Core Operations
          Hotel reservations, room inventory, check-in and check-out.

        • Management Operations
          Customer management, employee management,
          and administrative functions.

        • Financial Operations
          Payments, refunds, and payment processing.

        All responses are returned in JSON format.
        Standard error responses follow a unified API error schema.

        Authentication

        All secured endpoints require a valid JWT Bearer Token.
        """;

    /* ============================================================
     * AUTHOR & PROJECT INFORMATION
     * ============================================================ */
    // Đổi tên để hiển thị nhãn đẹp mắt hơn trên Swagger UI
    public static final String CONTACT_NAME = "Nguyen Phu Thinh";
    public static final String CONTACT_EMAIL = "nphuthinhvn@gmail.com";
    public static final String PROJECT_REPOSITORY  = "https://github.com/PhuThinh64/hotel-booking-system";

    /* ============================================================
     * LICENSE & TERMS
     * ============================================================ */
    public static final String LICENSE_NAME = "MIT License";
    public static final String LICENSE_URL = "https://opensource.org/licenses/MIT";

    /* ============================================================
     * SECURITY SCHEME CONFIGURATION
     * ============================================================ */
    public static final String SECURITY_SCHEME = "bearerAuth";
    public static final String JWT_SCHEME = "bearer";
    public static final String JWT_BEARER_FORMAT = "JWT";
    public static final String JWT_DESCRIPTION = """
        JWT Bearer Authentication.

        Enter your JWT access token using the following format:

        Bearer <your-access-token>
        """;
}