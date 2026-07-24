package com.example.hotel_booking.service.dto;

import com.example.hotel_booking.common.ServiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "Response payload containing extra service details")
public class ExtraServiceResponse {

    @Schema(description = "Unique identifier of the extra service", example = "1")
    private Long id;

    @Schema(description = "Name of the extra service", example = "Laundry Service")
    private String name;

    @Schema(description = "Price of the service", example = "150000.00")
    private BigDecimal price;

    @Schema(description = "Detailed description of the service", example = "Express laundry service within 24 hours")
    private String description;

    @Schema(description = "Type category of the service", example = "REGULAR")
    private ServiceType serviceType;

    @Schema(description = "Active status of the service in the system", example = "true")
    private Boolean active;
}