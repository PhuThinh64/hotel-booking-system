package com.example.hotel_booking.bookingroom.repository;

import com.example.hotel_booking.bookingroom.entity.BookingRoom;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRoomRepository extends JpaRepository<BookingRoom, Long> {

    List<BookingRoom> findAllByBookingId(Long bookingId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT COUNT(br)
        FROM BookingRoom br
        JOIN br.booking b
        WHERE br.roomType.id = :roomTypeId
        AND b.status IN ('PENDING_DEPOSIT', 'CONFIRMED', 'CHECKED_IN')
        AND (br.status IS NULL OR br.status <> 'CANCELLED')
        AND b.arrivalDate < :checkOut
        AND b.departureDate > :checkIn
    """)
    long countBookedByRoomType(
            @Param("roomTypeId") Long roomTypeId,
            @Param("checkIn") LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut
    );

    @Query("""
    SELECT COUNT(br)
    FROM BookingRoom br
    JOIN br.booking b
    WHERE br.roomType.id = :roomTypeId
    AND b.status IN ('PENDING_DEPOSIT', 'CONFIRMED', 'CHECKED_IN')
    AND (br.status IS NULL OR br.status <> 'CANCELLED')
    AND b.arrivalDate < :checkOut
    AND b.departureDate > :checkIn
    AND b.id <> :bookingId
""")
    long countBookedByRoomTypeExcludeCurrent(
            @Param("roomTypeId") Long roomTypeId,
            @Param("checkIn") LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut,
            @Param("bookingId") Long bookingId
    );


}