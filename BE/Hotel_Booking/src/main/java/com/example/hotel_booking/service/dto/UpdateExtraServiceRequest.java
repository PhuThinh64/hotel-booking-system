package com.example.hotel_booking.service.dto;

import com.example.hotel_booking.common.ServiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Request body for updating an existing extra service")
public class UpdateExtraServiceRequest {

    @Schema(description = "Name of the extra service", example = "Laundry Service Premium")
    @NotBlank(message = "SERVICE_NAME_REQUIRED")
    private String name;

    @Schema(description = "Price of the service", example = "200000.00")
    @NotNull(message = "PRICE_REQUIRED")
    @PositiveOrZero(message = "PRICE_INVALID")
    private BigDecimal price;

    @Schema(description = "Detailed description of the extra service", example = "Express laundry service within 6 hours")
    private String description;

    @Schema(description = "Active status of the service", example = "true")
    private Boolean active;

    @Schema(description = "Type category of the service", example = "REGULAR")
    private ServiceType serviceType;
}