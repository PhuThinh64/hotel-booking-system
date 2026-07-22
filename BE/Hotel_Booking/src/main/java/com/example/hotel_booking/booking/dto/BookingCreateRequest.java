package com.example.hotel_booking.booking.dto;

import com.example.hotel_booking.common.PaymentMethod;
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
public class BookingCreateRequest {
    private Long customerId;

    @NotBlank(message = "Họ và tên khách hàng không được để trống")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Họ và tên chứa ký tự không hợp lệ")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại không hợp lệ (phải từ 10 đến 11 số)")
    private String phoneNumber;

    @NotBlank(message = "Tên người liên hệ không được để trống")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Tên người liên hệ chứa ký tự không hợp lệ")
    private String contactName;

    @NotBlank(message = "Số điện thoại người liên hệ không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại người liên hệ không hợp lệ (phải từ 10 đến 11 số)")
    private String contactPhone;

    @NotEmpty(message = "Danh sách phòng đặt không được để trống")
    private List<BookingRoomRequest> roomTypes;

    private List<BookingServiceRequest> services;

    @NotNull(message = "Ngày nhận phòng không được để trống")
    @FutureOrPresent(message = "Ngày nhận phòng phải ở hiện tại hoặc tương lai")
    private LocalDateTime checkIn;

    @NotNull(message = "Ngày trả phòng không được để trống")
    private LocalDateTime checkOut;

    private BigDecimal depositAmount;
    private PaymentMethod paymentMethod;

}