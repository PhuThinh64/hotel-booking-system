package com.example.hotel_booking.service.dto;

import com.example.hotel_booking.common.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class ExtraServiceResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private ServiceType serviceType;
    private Boolean active;
}