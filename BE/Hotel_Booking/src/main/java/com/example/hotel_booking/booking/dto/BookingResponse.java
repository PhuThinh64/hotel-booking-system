package com.example.hotel_booking.booking.dto;

import com.example.hotel_booking.bookingroom.dto.BookingRoomResponse;
import com.example.hotel_booking.bookingservicedetail.dto.BookingServiceDetailResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Full booking details returned after creation or retrieval.")
public class BookingResponse {

    @Schema(description = "Unique booking identifier.", example = "25")
    private Long id;

    @Schema(description = "System-generated booking reference code.", example = "BK202607220001")
    private String bookingCode;

    @Schema(description = "Unique identifier of the associated customer account.", example = "15")
    private Long customerId;

    @Schema(description = "Full name of the guest.", example = "Nguyen Van A")
    private String customerName;

    @Schema(description = "Phone number of the guest.", example = "0912345678")
    private String customerPhone;

    @Schema(description = "Full name of the contact person.", example = "Tran Thi B")
    private String contactName;

    @Schema(description = "Phone number of the contact person.", example = "0987654321")
    private String contactPhone;

    @Schema(description = "Current booking status.", example = "CONFIRMED")
    private String status;

    @Schema(description = "Total charge for all booked rooms.", example = "2000000")
    private BigDecimal roomAmount;

    @Schema(description = "Total charge for all additional services.", example = "300000")
    private BigDecimal serviceAmount;

    @Schema(description = "Grand total of room and service charges.", example = "2300000")
    private BigDecimal totalAmount;

    @Schema(description = "Remaining balance after deducting the deposit.", example = "1300000")
    private BigDecimal remainingAmount;

    @Schema(description = "Deposit amount paid at booking time.", example = "1000000")
    private BigDecimal depositAmount;

    @Schema(description = "Refund method applied upon cancellation.", example = "BANK_TRANSFER")
    private String refundMethod;

    @Schema(description = "Amount refunded to the guest upon cancellation.", example = "500000")
    private BigDecimal refundAmount;

    @Schema(description = "Late check-out or other surcharge applied.", example = "150000")
    private BigDecimal surchargeAmount;

    @Schema(description = "Reason provided when the booking was cancelled.", example = "Customer changed travel plan.")
    private String cancelReason;

    @Schema(description = "Date and time the booking record was created.", example = "2026-07-22T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Date and time the booking record was last updated.", example = "2026-07-22T11:30:00")
    private LocalDateTime updateAt;

    @Schema(description = "Deadline by which the deposit must be paid to keep the booking active.", example = "2026-07-23T10:00:00")
    private LocalDateTime expiryDate;

    @Schema(description = "Scheduled check-in date and time.", example = "2026-08-15T14:00:00")
    private LocalDateTime arrivalDate;

    @Schema(description = "Scheduled check-out date and time.", example = "2026-08-17T12:00:00")
    private LocalDateTime departureDate;

    @Schema(description = "Actual date and time the guest checked in.", example = "2026-08-15T14:25:00")
    private LocalDateTime actualCheckIn;

    @Schema(description = "Actual date and time the guest checked out.", example = "2026-08-17T12:10:00")
    private LocalDateTime actualCheckOut;

    @Schema(description = "Payment method used for the booking.", example = "VNPAY")
    private String paymentMethod;

    @Schema(description = "Payment gateway URL for online payment. Populated when payment method is VNPAY.", example = "https://sandbox.vnpayment.vn/paymentv2/...")
    private String paymentUrl;

    @Schema(description = "List of booked room details associated with this booking.")
    private List<BookingRoomResponse> bookingRooms;

    @Schema(description = "List of additional service details associated with this booking.")
    private List<BookingServiceDetailResponse> bookingServices;
}