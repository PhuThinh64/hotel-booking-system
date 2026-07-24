package com.example.hotel_booking.booking.dto;

import com.example.hotel_booking.common.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating a new booking.")
public class BookingCreateRequest {

    @Schema(
            description = "Customer account identifier. Optional if booking is made for a walk-in guest.",
            example = "15"
    )
    private Long customerId;

    @Schema(
            description = "Full name of the guest.",
            example = "Nguyen Van A"
    )
    @NotBlank(message = "Họ và tên khách hàng không được để trống")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Họ và tên chứa ký tự không hợp lệ")
    private String fullName;

    @Schema(
            description = "Phone number of the guest.",
            example = "0912345678"
    )
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại không hợp lệ (phải từ 10 đến 11 số)")
    private String phoneNumber;

    @Schema(
            description = "Full name of the contact person.",
            example = "Tran Thi B"
    )
    @NotBlank(message = "Tên người liên hệ không được để trống")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Tên người liên hệ chứa ký tự không hợp lệ")
    private String contactName;

    @Schema(
            description = "Phone number of the contact person.",
            example = "0987654321"
    )
    @NotBlank(message = "Số điện thoại người liên hệ không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại người liên hệ không hợp lệ (phải từ 10 đến 11 số)")
    private String contactPhone;

    @Schema(
            description = "List of room types and quantities to be booked."
    )
    @NotEmpty(message = "Danh sách phòng đặt không được để trống")
    private List<BookingRoomRequest> roomTypes;

    @Schema(
            description = "List of additional services requested for the booking."
    )
    private List<BookingServiceRequest> services;

    @Schema(
            description = "Booking check-in date and time.",
            example = "2026-08-15T14:00:00"
    )
    @NotNull(message = "Ngày nhận phòng không được để trống")
    @FutureOrPresent(message = "Ngày nhận phòng phải ở hiện tại hoặc tương lai")
    private LocalDateTime checkIn;

    @Schema(
            description = "Booking check-out date and time.",
            example = "2026-08-17T12:00:00"
    )
    @NotNull(message = "Ngày trả phòng không được để trống")
    private LocalDateTime checkOut;

    @Schema(
            description = "Deposit amount paid at the time of booking.",
            example = "1000000"
    )
    private BigDecimal depositAmount;

    @Schema(
            description = "Payment method used for the deposit.",
            example = "VNPAY"
    )
    private PaymentMethod paymentMethod;
}