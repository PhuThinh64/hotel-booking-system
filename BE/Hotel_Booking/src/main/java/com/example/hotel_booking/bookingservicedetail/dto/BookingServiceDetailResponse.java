package com.example.hotel_booking.bookingservicedetail.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingServiceDetailResponse {
    private Long id;
    private Long bookingId;
    private String serviceName;
    private String serviceType;
    private Integer quantity;
    private BigDecimal priceAtOrder;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;
    private String status;
}