package com.example.hotel_booking.bookingservicedetail.repository;

import com.example.hotel_booking.bookingservicedetail.entity.BookingServiceDetail;
import com.example.hotel_booking.common.BookingServiceStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingServiceDetailRepository extends JpaRepository<BookingServiceDetail, Long> {
    @EntityGraph(attributePaths = {"extraService"})
    List<BookingServiceDetail> findAllByBookingId(Long bookingId);

    Optional<BookingServiceDetail> findByBookingIdAndExtraServiceIdAndStatus(Long bookingId, Long serviceId, BookingServiceStatus status);


}