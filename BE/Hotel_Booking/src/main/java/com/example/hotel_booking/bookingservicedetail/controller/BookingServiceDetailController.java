package com.example.hotel_booking.bookingservicedetail.controller;

import com.example.hotel_booking.bookingservicedetail.dto.BookingServiceCreateRequest;
import com.example.hotel_booking.bookingservicedetail.dto.BookingServiceDetailResponse;
import com.example.hotel_booking.bookingservicedetail.dto.BookingServiceUpdateRequest;
import com.example.hotel_booking.bookingservicedetail.service.BookingServiceDetailService;
import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.config.swagger.constants.ApiInfoConstants;
import com.example.hotel_booking.config.swagger.constants.SwaggerResponseMessages;
import com.example.hotel_booking.config.swagger.constants.SwaggerTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/booking-service-details")
@RequiredArgsConstructor
@Tag(
        name = SwaggerTags.BOOKING_SERVICE_DETAIL,
        description = "Manage extra service items attached to specific bookings, including ordering, updating quantities, and cancellations."
)
@SecurityRequirement(name = ApiInfoConstants.SECURITY_SCHEME)
public class BookingServiceDetailController {

    private final BookingServiceDetailService serviceDetailService;

    @Operation(
            summary = "Add Extra Service to Booking",
            description = "Order and attach an extra service with a specified quantity to an existing booking."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = SwaggerResponseMessages.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<BookingServiceDetailResponse> add(
            @RequestBody @Valid BookingServiceCreateRequest request
    ) {
        return ApiResponse.<BookingServiceDetailResponse>builder()
                .result(serviceDetailService.addService(request))
                .build();
    }

    @Operation(
            summary = "Get Services by Booking ID",
            description = "Retrieve all ordered extra service details associated with a given booking ID."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = SwaggerResponseMessages.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            )
    })
    @GetMapping("/booking/{bookingId}")
    public ApiResponse<List<BookingServiceDetailResponse>> getByBooking(
            @Parameter(description = "Booking ID", example = "100")
            @PathVariable Long bookingId
    ) {
        return ApiResponse.<List<BookingServiceDetailResponse>>builder()
                .result(serviceDetailService.getByBookingId(bookingId))
                .build();
    }

    @Operation(
            summary = "Cancel Booking Service",
            description = "Cancel an ordered extra service item from a booking by its detail ID."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = SwaggerResponseMessages.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            )
    })
    @PatchMapping("/cancel/{id}")
    public ApiResponse<BookingServiceDetailResponse> cancel(
            @Parameter(description = "Booking Service Detail ID", example = "15")
            @PathVariable Long id
    ) {
        return ApiResponse.<BookingServiceDetailResponse>builder()
                .result(serviceDetailService.cancelService(id))
                .build();
    }

    @Operation(
            summary = "Update Service Quantity",
            description = "Update the quantity of an ordered extra service item by its detail ID."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = SwaggerResponseMessages.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            )
    })
    @PutMapping("/{id}")
    public ApiResponse<BookingServiceDetailResponse> update(
            @Parameter(description = "Booking Service Detail ID", example = "15")
            @PathVariable Long id,

            @RequestBody @Valid BookingServiceUpdateRequest request
    ) {
        return ApiResponse.<BookingServiceDetailResponse>builder()
                .result(serviceDetailService.updateService(id, request))
                .build();
    }
}