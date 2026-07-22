package com.example.hotel_booking.bookingroom.service;

import com.example.hotel_booking.bookingroom.dto.BookingRoomCreateRequest;
import com.example.hotel_booking.bookingroom.dto.BookingRoomResponse;

public interface BookingRoomService {
    BookingRoomResponse addRoom(BookingRoomCreateRequest request);
    BookingRoomResponse assignRoom(Long id, Long roomId);
    BookingRoomResponse changeRoomType(Long id, Long newRoomTypeId);
}