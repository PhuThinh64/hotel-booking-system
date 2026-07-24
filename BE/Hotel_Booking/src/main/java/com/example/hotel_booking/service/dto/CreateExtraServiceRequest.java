package com.example.hotel_booking.service.dto;

import com.example.hotel_booking.common.ServiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Request body for creating a new extra service")
public class CreateExtraServiceRequest {

    @Schema(description = "Name of the extra service", example = "Laundry Service")
    @NotBlank(message = "SERVICE_NAME_REQUIRED")
    private String name;

    @Schema(description = "Price of the service", example = "150000.00")
    @NotNull(message = "PRICE_REQUIRED")
    @Positive(message = "PRICE_MUST_BE_POSITIVE")
    private BigDecimal price;

    @Schema(description = "Detailed description of the extra service", example = "Express laundry service within 24 hours")
    private String description;

    @Schema(description = "Type category of the service", example = "REGULAR")
    private ServiceType serviceType = ServiceType.REGULAR;
}