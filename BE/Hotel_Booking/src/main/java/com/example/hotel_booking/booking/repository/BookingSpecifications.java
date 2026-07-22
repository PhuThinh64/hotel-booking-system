package com.example.hotel_booking.booking.repository;

import com.example.hotel_booking.booking.entity.Booking;
import com.example.hotel_booking.common.BookingStatus;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public class BookingSpecifications {

    public static Specification<Booking> hasBookingCode(String code) {
        return (root, query, cb) -> code == null || code.isEmpty() ? null
                : cb.like(root.get("bookingCode"), "%" + code + "%");
    }

    public static Specification<Booking> hasStatus(BookingStatus status) {
        return (root, query, cb) -> status == null ? null
                : cb.equal(root.get("status"), status);
    }

    public static Specification<Booking> hasDateRange(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start == null || end == null) return null;
            return cb.between(root.get("arrivalDate"), start, end);
        };
    }
}