package com.example.hotel_booking.booking.controller;

import com.example.hotel_booking.booking.dto.*;
import com.example.hotel_booking.booking.service.BookingService;
import com.example.hotel_booking.common.BookingStatus;
import com.example.hotel_booking.common.PaymentMethod;
import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.config.swagger.constants.ApiInfoConstants;
import com.example.hotel_booking.config.swagger.constants.ParameterDescriptions;
import com.example.hotel_booking.config.swagger.constants.SwaggerResponseMessages;
import com.example.hotel_booking.config.swagger.constants.SwaggerTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
@Tag(
        name = SwaggerTags.BOOKING,
        description = "Manage hotel bookings including reservation, payment processing, cancellation, refund handling, check-in, and check-out operations."
)
@SecurityRequirement(name = ApiInfoConstants.SECURITY_SCHEME)
public class BookingController {

    private final BookingService bookingService;

    @Operation(
            summary = "Retrieve Current User Booking History",
            description = "Retrieve the booking history of the currently authenticated customer."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.BOOKING_HISTORY_FOUND
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = SwaggerResponseMessages.UNAUTHORIZED
            )
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my-history")
    public ApiResponse<List<BookingResponse>> getMyHistory() {

        return ApiResponse.<List<BookingResponse>>builder()
                .result(bookingService.getMyHistory())
                .build();
    }

    @Operation(
            summary = "Lookup Booking by Phone Number and Booking Code",
            description = "Retrieve booking information using the customer's phone number and booking code without requiring authentication.",
            security = {}
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.BOOKING_FOUND
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            )
    })
    @GetMapping("/lookup")
    public ApiResponse<BookingResponse> lookupBooking(

            @Parameter(
                    description = ParameterDescriptions.PHONE,
                    example = "0912345678"
            )
            @RequestParam
            String phone,

            @Parameter(
                    description = ParameterDescriptions.BOOKING_CODE,
                    example = "BK202607220001"
            )
            @RequestParam
            String bookingCode
    ) {

        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.getBookingByPhoneAndCode(phone, bookingCode))
                .build();
    }

    @Operation(
            summary = "Create a New Booking",
            description = "Create a new booking after validating room availability, preventing overbooking, generating booking information, and initializing the payment process."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = SwaggerResponseMessages.BOOKING_CREATED
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
                    responseCode = "403",
                    description = SwaggerResponseMessages.FORBIDDEN
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = SwaggerResponseMessages.CONFLICT
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<BookingResponse> createBooking(

            @Valid
            @RequestBody
            BookingCreateRequest request
    ) {

        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.createBooking(request))
                .build();
    }

    @Operation(
            summary = "Retrieve Booking List",
            description = "Retrieve a paginated list of bookings with optional filtering by booking code, booking status, and booking date range."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.BOOKINGS_FOUND
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = SwaggerResponseMessages.UNAUTHORIZED
            )
    })
    @GetMapping
    public ApiResponse<Page<BookingResponse>> getAllBookings(

            @Parameter(
                    description = ParameterDescriptions.BOOKING_CODE,
                    example = "BK202607220001"
            )
            @RequestParam(required = false)
            String code,

            @Parameter(
                    description = "Booking status filter.",
                    example = "CONFIRMED"
            )
            @RequestParam(required = false)
            BookingStatus status,

            @Parameter(
                    description = "Start of booking date range.",
                    example = "2026-07-01T00:00:00"
            )
            @RequestParam(required = false)
            LocalDateTime start,

            @Parameter(
                    description = "End of booking date range.",
                    example = "2026-07-31T23:59:59"
            )
            @RequestParam(required = false)
            LocalDateTime end,

            Pageable pageable
    ) {

        return ApiResponse.<Page<BookingResponse>>builder()
                .result(bookingService.getAllBookings(code, status, start, end, pageable))
                .build();
    }

    @Operation(
            summary = "Retrieve Booking Details",
            description = "Retrieve detailed information of a specific booking by its identifier."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.BOOKING_FOUND
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            )
    })
    @GetMapping("/{id}")
    public ApiResponse<BookingResponse> getBookingById(

            @Parameter(
                    description = ParameterDescriptions.BOOKING_ID,
                    example = "25"
            )
            @PathVariable
            Long id
    ) {

        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.getBookingById(id))
                .build();
    }

    @Operation(
            summary = "Cancel a Booked Room",
            description = "Cancel a specific booked room within an existing booking and calculate the applicable refund amount."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.BOOKED_ROOM_CANCELLED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = SwaggerResponseMessages.CONFLICT
            )
    })
    @PostMapping("/{id}/cancel-room/{bookingRoomId}")
    public ApiResponse<BookingCancelResponse> cancelSingleRoom(

            @Parameter(
                    description = ParameterDescriptions.BOOKING_ID,
                    example = "25"
            )
            @PathVariable
            Long id,

            @Parameter(
                    description = ParameterDescriptions.ROOM_ID,
                    example = "12"
            )
            @PathVariable
            Long bookingRoomId,

            @Parameter(
                    description = "Refund method.",
                    example = "CASH"
            )
            @RequestParam(required = false)
            String refundMethod,

            @Parameter(
                    description = "Cancellation reason.",
                    example = "Customer changed travel plan."
            )
            @RequestParam(required = false)
            String reason
    ) {

        return ApiResponse.<BookingCancelResponse>builder()
                .result(bookingService.cancelSingleRoom(id, bookingRoomId, refundMethod, reason))
                .build();
    }

    @Operation(
            summary = "Cancel Entire Booking",
            description = "Cancel the entire booking and process the refund according to the cancellation policy."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.BOOKING_CANCELLED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = SwaggerResponseMessages.CONFLICT
            )
    })
    @PostMapping("/{id}/cancel-full")
    public ApiResponse<BookingCancelResponse> cancelFullBooking(

            @Parameter(
                    description = ParameterDescriptions.BOOKING_ID,
                    example = "25"
            )
            @PathVariable
            Long id,

            @Parameter(
                    description = "Refund method.",
                    example = "BANK_TRANSFER"
            )
            @RequestParam(required = false)
            PaymentMethod refundMethod,

            @Parameter(
                    description = "Cancellation reason.",
                    example = "Customer requested cancellation."
            )
            @RequestParam(required = false)
            String reason
    ) {

        return ApiResponse.<BookingCancelResponse>builder()
                .result(bookingService.cancelFullBooking(id, refundMethod, reason))
                .build();
    }

    @Operation(
            summary = "Approve Manual Refund",
            description = "Approve and complete a manual refund for a cancelled booking."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.MANUAL_REFUND_APPROVED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            )
    })
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @PostMapping("/{id}/approve-manual-refund")
    public ApiResponse<String> approveManualRefund(

            @Parameter(
                    description = ParameterDescriptions.BOOKING_ID,
                    example = "25"
            )
            @PathVariable
            Long id,

            @Parameter(
                    description = "Refund method.",
                    example = "CASH"
            )
            @RequestParam
            PaymentMethod refundMethod
    ) {

        bookingService.approveManualRefund(id, refundMethod);

        return ApiResponse.<String>builder()
                .result(SwaggerResponseMessages.MANUAL_REFUND_APPROVED)
                .build();
    }

    @Operation(
            summary = "Confirm Booking Deposit",
            description = "Confirm the customer's deposit payment and update the booking payment status."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.DEPOSIT_CONFIRMED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            )
    })
    @PostMapping("/{bookingId}/confirm-deposit")
    public ApiResponse<BookingResponse> confirmDeposit(

            @Parameter(
                    description = ParameterDescriptions.BOOKING_ID,
                    example = "25"
            )
            @PathVariable
            Long bookingId,

            @Parameter(
                    description = "Payment method.",
                    example = "VNPAY"
            )
            @RequestParam
            PaymentMethod method,

            @Parameter(
                    description = "Payment transaction identifier.",
                    example = "VNP202607220001"
            )
            @RequestParam(required = false)
            String transactionId
    ) {

        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.confirmDeposit(bookingId, method, transactionId))
                .build();
    }

    @Operation(
            summary = "Check-in Customer",
            description = "Perform customer check-in and update the booking status."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.CHECKIN_SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            )
    })
    @PostMapping("/{id}/check-in")
    public ApiResponse<BookingResponse> checkIn(

            @Parameter(
                    description = ParameterDescriptions.BOOKING_ID,
                    example = "25"
            )
            @PathVariable
            Long id
    ) {

        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.checkIn(id))
                .build();
    }

    @Operation(
            summary = "Preview Booking Checkout",
            description = "Generate a checkout preview including room charges, service charges, refunds, and remaining balance."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            )
    })
    @GetMapping("/{id}/checkout-preview")
    public ApiResponse<CheckoutPreviewResponse> previewCheckout(

            @Parameter(
                    description = ParameterDescriptions.BOOKING_ID,
                    example = "25"
            )
            @PathVariable
            Long id
    ) {

        return ApiResponse.<CheckoutPreviewResponse>builder()
                .result(bookingService.previewCheckout(id))
                .build();
    }

    @Operation(
            summary = "Check-out Customer",
            description = "Complete customer checkout, calculate the final payment, process refunds if necessary, and finalize the booking."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.CHECKOUT_SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            )
    })
    @PostMapping("/{id}/check-out")
    public ApiResponse<CheckoutResponse> checkOut(

            @Parameter(
                    description = ParameterDescriptions.BOOKING_ID,
                    example = "25"
            )
            @PathVariable
            Long id,

            @Parameter(
                    description = "Payment method for checkout.",
                    example = "VNPAY"
            )
            @RequestParam
            String paymentMethod,

            @Parameter(
                    description = "Refund method if applicable.",
                    example = "CASH"
            )
            @RequestParam(required = false)
            String refundMethod,

            jakarta.servlet.http.HttpServletRequest request
    ) {

        String ipAddress = request.getRemoteAddr();

        if ("0:0:0:0:0:0:0:1".equals(ipAddress)) {
            ipAddress = "127.0.0.1";
        }

        return ApiResponse.<CheckoutResponse>builder()
                .result(bookingService.checkOut(id, paymentMethod, refundMethod, ipAddress))
                .build();
    }
}