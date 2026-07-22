package com.example.hotel_booking.bookingroom.dto;

import com.example.hotel_booking.common.BookingRoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRoomResponse {
    private Long bookingRoomId;
    private Long roomId;
    private String roomNumber;
    private BigDecimal priceAtOrder;
    private String roomType;
    private BookingRoomStatus status;
}