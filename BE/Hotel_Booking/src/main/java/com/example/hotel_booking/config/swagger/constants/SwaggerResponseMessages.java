package com.example.hotel_booking.config.swagger.constants;

public final class SwaggerResponseMessages {

    private SwaggerResponseMessages(){}

    // Common

    public static final String SUCCESS =
            "Request processed successfully.";

    public static final String CREATED =
            "Resource created successfully.";

    public static final String UPDATED =
            "Resource updated successfully.";

    public static final String DELETED =
            "Resource deleted successfully.";

    // Error

    public static final String BAD_REQUEST =
            "Validation failed or request payload is invalid.";

    public static final String UNAUTHORIZED =
            "Authentication required.";

    public static final String FORBIDDEN =
            "You do not have permission to access this resource.";

    public static final String NOT_FOUND =
            "Requested resource was not found.";

    public static final String CONFLICT =
            "Business rule conflict.";

    public static final String INTERNAL_SERVER_ERROR =
            "Unexpected server error occurred.";

    // Booking

    public static final String BOOKING_CREATED =
            "Booking created successfully.";

    public static final String BOOKING_FOUND =
            "Booking retrieved successfully.";

    public static final String BOOKINGS_FOUND =
            "Booking list retrieved successfully.";

    public static final String BOOKING_HISTORY_FOUND =
            "Booking history retrieved successfully.";

    public static final String BOOKING_CANCELLED =
            "Booking cancelled successfully.";

    public static final String BOOKED_ROOM_CANCELLED =
            "Booked room cancelled successfully.";

    public static final String ROOM_UNAVAILABLE =
            "Selected room is no longer available.";

    public static final String MANUAL_REFUND_APPROVED =
            "Manual refund approved successfully.";

    public static final String DEPOSIT_CONFIRMED =
            "Deposit confirmed successfully.";

    public static final String CHECKIN_SUCCESS =
            "Customer checked in successfully.";

    public static final String CHECKOUT_SUCCESS =
            "Customer checked out successfully.";

}