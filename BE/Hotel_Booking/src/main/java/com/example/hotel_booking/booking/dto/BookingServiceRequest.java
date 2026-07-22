package com.example.hotel_booking.booking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingServiceRequest {
    private Long serviceId;
    private Integer quantity;
}