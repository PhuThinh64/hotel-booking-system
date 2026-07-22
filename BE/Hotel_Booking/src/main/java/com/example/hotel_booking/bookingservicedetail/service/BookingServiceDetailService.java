package com.example.hotel_booking.bookingservicedetail.service;

import com.example.hotel_booking.bookingservicedetail.dto.BookingServiceCreateRequest;
import com.example.hotel_booking.bookingservicedetail.dto.BookingServiceDetailResponse;
import com.example.hotel_booking.bookingservicedetail.dto.BookingServiceUpdateRequest;

import java.util.List;

public interface BookingServiceDetailService {
    BookingServiceDetailResponse addService(BookingServiceCreateRequest request);
    BookingServiceDetailResponse cancelService(Long id);
    List<BookingServiceDetailResponse> getByBookingId(Long bookingId);
    BookingServiceDetailResponse updateService(Long id, BookingServiceUpdateRequest request);
}