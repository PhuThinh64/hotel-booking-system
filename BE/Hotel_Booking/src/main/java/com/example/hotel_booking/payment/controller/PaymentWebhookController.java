package com.example.hotel_booking.payment.controller;

import com.example.hotel_booking.booking.service.BookingService;
import com.example.hotel_booking.common.PaymentMethod;
import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.common.exception.AppException;
import com.example.hotel_booking.common.exception.ErrorCode;
import com.example.hotel_booking.config.swagger.constants.ApiInfoConstants;
import com.example.hotel_booking.config.swagger.constants.SwaggerResponseMessages;
import com.example.hotel_booking.config.swagger.constants.SwaggerTags;
import com.example.hotel_booking.payment.repository.PaymentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(
        name = SwaggerTags.PAYMENT,
        description = "Manage hotel payments, including processing bank webhooks."
)
@SecurityRequirement(name = ApiInfoConstants.SECURITY_SCHEME)
public class PaymentWebhookController {

    private final BookingService bookingService;
    private final PaymentRepository paymentRepo;

    @Operation(
            summary = "Process Bank Transfer Webhook",
            description = "Process incoming transaction callback payload from bank integration to confirm payments.",
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            )
    })
    @PostMapping("/webhook")
    public ApiResponse<String> handleBankWebhook(
            @Parameter(
                    description = "Raw webhook payload containing transaction description and ID."
            )
            @RequestBody
            Map<String, Object> payload
    ) {
        String description = (String) payload.get("description");
        String transactionId = payload.get("transactionId").toString();

        
        String txnRef = extractTransactionId(description);

        if (txnRef.isEmpty()) {
            throw new AppException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        
        return paymentRepo.findByTransactionId(txnRef)
                .map(payment -> {
                    
                    bookingService.confirmDeposit(
                            payment.getBooking().getId(),
                            PaymentMethod.BANK_TRANSFER,
                            txnRef
                    );

                    return ApiResponse.<String>builder()
                            .result("Xác nhận thanh toán thành công cho đơn " + payment.getBooking().getId())
                            .build();
                })
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    
    private String extractTransactionId(String desc) {
        
        Pattern pattern = Pattern.compile("(CASH|DEPO|VNPAY)-\\d+");
        Matcher matcher = pattern.matcher(desc.toUpperCase());
        return matcher.find() ? matcher.group() : "";
    }
}