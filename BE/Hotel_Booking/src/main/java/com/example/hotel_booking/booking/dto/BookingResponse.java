package com.example.hotel_booking.booking.dto;

import com.example.hotel_booking.bookingroom.dto.BookingRoomResponse;
import com.example.hotel_booking.bookingservicedetail.dto.BookingServiceDetailResponse;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String bookingCode;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String contactName;
    private String contactPhone;
    private String status;
    private BigDecimal roomAmount;
    private BigDecimal serviceAmount;
    private BigDecimal totalAmount;
    private BigDecimal remainingAmount;
    private BigDecimal depositAmount;
    private String refundMethod;
    private BigDecimal refundAmount;
    private BigDecimal surchargeAmount;
    private String cancelReason;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private LocalDateTime expiryDate;
    private LocalDateTime arrivalDate;
    private LocalDateTime departureDate;
    private LocalDateTime actualCheckIn;
    private LocalDateTime actualCheckOut;
    private String paymentMethod;
    private String paymentUrl;
    private List<BookingRoomResponse> bookingRooms;
    private List<BookingServiceDetailResponse>  bookingServices;
}