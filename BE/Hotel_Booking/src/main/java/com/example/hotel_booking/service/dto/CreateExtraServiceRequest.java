package com.example.hotel_booking.service.dto;

import com.example.hotel_booking.common.ServiceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateExtraServiceRequest {
    @NotBlank(message = "SERVICE_NAME_REQUIRED")
    private String name;

    @NotNull(message = "PRICE_REQUIRED")
    @Positive(message = "PRICE_MUST_BE_POSITIVE")
    private BigDecimal price;

    private String description;

    private ServiceType serviceType = ServiceType.REGULAR;
}