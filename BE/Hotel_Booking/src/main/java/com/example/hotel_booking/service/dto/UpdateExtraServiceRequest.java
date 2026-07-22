package com.example.hotel_booking.service.dto;

import com.example.hotel_booking.common.ServiceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class UpdateExtraServiceRequest {
    @NotBlank(message = "SERVICE_NAME_REQUIRED")
    private String name;

    @NotNull(message = "PRICE_REQUIRED")
    @PositiveOrZero(message = "PRICE_INVALID")
    private BigDecimal price;

    private String description;

    private Boolean active;

    private ServiceType serviceType;
}