package com.example.hotel_booking.booking.repository;

import com.example.hotel_booking.booking.entity.Booking;
import com.example.hotel_booking.common.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    List<Booking> findByStatusAndExpiryDateBefore(BookingStatus status, LocalDateTime now);

    @EntityGraph(attributePaths = {"customer", "bookingRooms", "bookingRooms.room"})
    Page<Booking> findAll(Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.contactPhone = :phone AND b.bookingCode = :bookingCode")
    Optional<Booking> findByPhoneAndBookingCode(@Param("phone") String phone, @Param("bookingCode") String bookingCode);

    @EntityGraph(attributePaths = {
            "bookingRooms",
            "bookingRooms.room",
            "bookingRooms.roomType"
    })
    @Query("SELECT b FROM Booking b WHERE b.customer.id = :customerId ORDER BY b.createdAt DESC")
    List<Booking> findByCustomerIdWithRooms(@Param("customerId") Long customerId);

    @Query("SELECT b FROM Booking b WHERE b.arrivalDate >= :start AND b.arrivalDate <= :end AND b.status = 'CONFIRMED'")
    List<Booking> findTodayCheckInList(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.arrivalDate >= :start AND b.arrivalDate <= :end AND b.status = 'CONFIRMED'")
    long countTodayCheckIns(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.departureDate >= :start AND b.departureDate <= :end AND b.status = 'CHECKED_IN'")
    List<Booking> findTodayCheckOutList(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.departureDate >= :start AND b.departureDate <= :end AND b.status = 'CHECKED_IN'")
    long countTodayCheckOuts(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.arrivalDate >= :start AND b.arrivalDate <= :end AND b.status IN :statuses")
    BigDecimal calculateTotalRevenueByDateRange(@Param("statuses") List<BookingStatus> statuses, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.arrivalDate >= :start AND b.arrivalDate <= :end AND b.status IN :statuses")
    long countSuccessfulBookingsByDateRange(@Param("statuses") List<BookingStatus> statuses, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    @Query("SELECT COUNT(b) FROM Booking b WHERE b.arrivalDate >= :start AND b.arrivalDate <= :end AND b.status = :status")
    long countByStatusAndDateRange(@Param("status") BookingStatus status, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    @Query("SELECT b.status, COUNT(b) FROM Booking b WHERE b.arrivalDate >= :start AND b.arrivalDate <= :end GROUP BY b.status")
    List<Object[]> countByEachStatusByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    @Query("""
        SELECT
            DATE(b.arrivalDate),
            COALESCE(SUM(b.totalAmount), 0)
        FROM Booking b
        WHERE b.status IN :statuses
        AND b.arrivalDate BETWEEN :startDate AND :endDate
        GROUP BY DATE(b.arrivalDate)
        ORDER BY DATE(b.arrivalDate)
        """)
    List<Object[]> getDailyRevenueByDateRange(
            @Param("statuses") List<BookingStatus> statuses,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    
    @Query("SELECT YEAR(b.arrivalDate), MONTH(b.arrivalDate), SUM(b.totalAmount) " +
            "FROM Booking b " +
            "WHERE b.arrivalDate >= :start AND b.arrivalDate <= :end " +
            "AND b.status IN :statuses " +
            "GROUP BY YEAR(b.arrivalDate), MONTH(b.arrivalDate) " +
            "ORDER BY YEAR(b.arrivalDate), MONTH(b.arrivalDate)")
    List<Object[]> getMonthlyRevenueByDateRange(@Param("statuses") List<BookingStatus> statuses,
                                                @Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end);

    
    @Query("SELECT COUNT(DISTINCT br.room.id) FROM BookingRoom br " +
            "WHERE br.booking.arrivalDate <= :end AND br.booking.departureDate >= :start " +
            "AND br.booking.status IN ('CHECKED_IN', 'CHECKED_OUT')")
    long countOccupiedRoomsInDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


}