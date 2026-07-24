package com.example.hotel_booking.payment.controller;

import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.config.swagger.constants.ApiInfoConstants;
import com.example.hotel_booking.config.swagger.constants.ParameterDescriptions;
import com.example.hotel_booking.config.swagger.constants.SwaggerResponseMessages;
import com.example.hotel_booking.config.swagger.constants.SwaggerTags;
import com.example.hotel_booking.payment.dto.PaymentResponse;
import com.example.hotel_booking.payment.dto.PendingRefundResponse;
import com.example.hotel_booking.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(
        name = SwaggerTags.PAYMENT,
        description = "Manage hotel payments, including processing transactions, viewing history, retrieving revenue analytics, and handling pending refunds."
)
@SecurityRequirement(name = ApiInfoConstants.SECURITY_SCHEME)
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "VNPay Payment Callback",
            description = "Process the payment gateway callback from VNPay after the transaction has been completed.",
            security = {}
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "302",
                    description = "Redirects to the transaction status page."
            )
    })
    @GetMapping("/vnpay-callback")
    public void vnpayCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            fields.put(fieldName, request.getParameter(fieldName));
        }

        
        String vnp_SecureHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        
        String resultRedirect = paymentService.handleVnPayCallback(fields, vnp_SecureHash);

        
        response.sendRedirect(resultRedirect);
    }

    @Operation(
            summary = "Retrieve Booking Payments",
            description = "Retrieve a list of payments associated with a specific booking identifier.",
            security = {}
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
    @GetMapping("/booking/{bookingId}")
    public ApiResponse<List<PaymentResponse>> getByBooking(
            @Parameter(
                    description = ParameterDescriptions.BOOKING_ID,
                    example = "25"
            )
            @PathVariable
            Long bookingId
    ) {
        return ApiResponse.<List<PaymentResponse>>builder()
                .result(paymentService.getPaymentsByBookingId(bookingId))
                .build();
    }

    @Operation(
            summary = "Retrieve Total Revenue",
            description = "Calculate and retrieve the total revenue generated within the specified date range.",
            security = {}
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            )
    })
    @GetMapping("/revenue")
    public ApiResponse<BigDecimal> getRevenue(
            @Parameter(
                    description = "Start date and time of the revenue period.",
                    example = "2026-07-01T00:00:00"
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @Parameter(
                    description = "End date and time of the revenue period.",
                    example = "2026-07-31T23:59:59"
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end
    ) {
        return ApiResponse.<BigDecimal>builder()
                .result(paymentService.getTotalRevenue(start, end))
                .build();
    }

    @Operation(
            summary = "Retrieve Pending Refunds",
            description = "Retrieve a paginated list of pending refunds based on keyword, status, payment method, and date range filters."
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
                    responseCode = "403",
                    description = SwaggerResponseMessages.FORBIDDEN
            )
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @GetMapping("/pending-refunds")
    public ApiResponse<Page<PendingRefundResponse>> getPendingRefunds(
            @Parameter(
                    description = "Page number (0-based index).",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    description = "Number of records per page.",
                    example = "10"
            )
            @RequestParam(defaultValue = "10")
            int size,

            @Parameter(
                    description = "Filter by payment/refund status.",
                    example = "PENDING"
            )
            @RequestParam(required = false)
            String status,

            @Parameter(
                    description = "Search keyword for filtering contact details.",
                    example = "Nguyen"
            )
            @RequestParam(required = false)
            String keyword,

            @Parameter(
                    description = "Filter by payment method.",
                    example = "BANK_TRANSFER"
            )
            @RequestParam(required = false)
            String method,

            @Parameter(
                    description = "Start date of the refund period (YYYY-MM-DD).",
                    example = "2026-07-01"
            )
            @RequestParam(required = false)
            String startDate,

            @Parameter(
                    description = "End date of the refund period (YYYY-MM-DD).",
                    example = "2026-07-31"
            )
            @RequestParam(required = false)
            String endDate
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return ApiResponse.<Page<PendingRefundResponse>>builder()
                .result(paymentService.getPendingRefunds(keyword, status, method, startDate, endDate, pageable))
                .build();
    }
}