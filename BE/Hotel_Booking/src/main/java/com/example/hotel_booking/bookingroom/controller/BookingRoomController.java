package com.example.hotel_booking.bookingroom.controller;

import com.example.hotel_booking.bookingroom.dto.BookingRoomCreateRequest;
import com.example.hotel_booking.bookingroom.dto.BookingRoomResponse;
import com.example.hotel_booking.bookingroom.service.BookingRoomService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/booking-rooms")
@RequiredArgsConstructor
@Tag(
        name = SwaggerTags.BOOKING_ROOM,
        description = "Manage room allocations within bookings, physical room assignments, and room type changes."
)
@SecurityRequirement(name = ApiInfoConstants.SECURITY_SCHEME)
public class BookingRoomController {

    private final BookingRoomService bookingRoomService;

    @Operation(
            summary = "Add Room to Booking",
            description = "Add a new room requirement (room type) to an existing active booking."
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
    @PostMapping("/add")
    public ApiResponse<BookingRoomResponse> addRoom(
            @RequestBody @Valid BookingRoomCreateRequest request
    ) {
        return ApiResponse.<BookingRoomResponse>builder()
                .result(bookingRoomService.addRoom(request))
                .build();
    }

    @Operation(
            summary = "Assign Physical Room",
            description = "Assign a specific physical room number to a booked room item."
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
    @PutMapping("/{id}/assign")
    public ApiResponse<BookingRoomResponse> assignRoom(
            @Parameter(description = "Booking Room Item ID", example = "10")
            @PathVariable Long id,

            @Parameter(description = "Physical Room ID to assign", example = "101")
            @RequestParam Long roomId
    ) {
        return ApiResponse.<BookingRoomResponse>builder()
                .result(bookingRoomService.assignRoom(id, roomId))
                .build();
    }

    @Operation(
            summary = "Change Room Type",
            description = "Change the room type category for a specific booking room item."
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
    @PutMapping("/{id}/change-type")
    public ApiResponse<BookingRoomResponse> changeRoomType(
            @Parameter(description = "Booking Room Item ID", example = "10")
            @PathVariable Long id,

            @Parameter(description = "New Room Type ID", example = "2")
            @RequestParam Long roomTypeId
    ) {
        return ApiResponse.<BookingRoomResponse>builder()
                .result(bookingRoomService.changeRoomType(id, roomTypeId))
                .build();
    }
}