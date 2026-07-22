package com.example.hotel_booking.payment.mapper;

import com.example.hotel_booking.booking.entity.Booking;
import com.example.hotel_booking.common.PaymentMethod;
import com.example.hotel_booking.common.PaymentStatus;
import com.example.hotel_booking.payment.dto.PaymentCreateRequest;
import com.example.hotel_booking.payment.dto.PaymentResponse;
import com.example.hotel_booking.payment.dto.PendingRefundResponse;
import com.example.hotel_booking.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", imports = {java.time.LocalDateTime.class})
public interface PaymentMapper {

    @Mapping(source = "booking.id", target = "bookingId")
    @Mapping(source = "booking.customer.fullName", target = "customerName")
    PaymentResponse toResponse(Payment payment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", ignore = true) 
    @Mapping(target = "paidAt", expression = "java(java.time.LocalDateTime.now())")
    Payment toEntity(PaymentCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", source = "booking")
    @Mapping(target = "amount", source = "booking.depositAmount")
    @Mapping(target = "method", source = "method")
    @Mapping(target = "status", constant = "SUCCESS")
    @Mapping(target = "paymentType", constant = "FINAL_PAYMENT")
    @Mapping(target = "paidAt", expression = "java(LocalDateTime.now())")
    Payment toDepositPayment(Booking booking, PaymentMethod method, PaymentStatus status, String transactionId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", source = "booking")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "method", source = "method")
    @Mapping(target = "status", constant = "SUCCESS")
    @Mapping(target = "paymentType", constant = "FINAL_PAYMENT")
    @Mapping(target = "paidAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "transactionId", expression = "java(\"OUT-\" + booking.getId() + \"-\" + System.currentTimeMillis())")
    Payment toFinalPayment(Booking booking, BigDecimal amount, PaymentMethod method);

    @Mapping(source = "id", target = "paymentId")
    @Mapping(source = "booking.id", target = "bookingId")
    @Mapping(source = "booking.contactName", target = "contactName")
    @Mapping(source = "booking.contactPhone", target = "contactPhone")
    PendingRefundResponse toPendingRefundResponse(Payment payment);
}
