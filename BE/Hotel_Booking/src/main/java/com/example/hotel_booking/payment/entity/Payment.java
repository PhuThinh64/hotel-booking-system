package com.example.hotel_booking.payment.entity;

import com.example.hotel_booking.audit.annotation.LogField;
import com.example.hotel_booking.booking.entity.Booking;
import com.example.hotel_booking.common.PaymentMethod;
import com.example.hotel_booking.common.PaymentStatus;
import com.example.hotel_booking.common.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payments_booking", columnList = "booking_id"),
        @Index(name = "idx_payments_transaction", columnList = "transaction_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Payment  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")

    private Booking booking;

    @LogField(name = "Số tiền")
    private BigDecimal amount;

    @LogField(name = "Loại thanh toán")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @LogField(name = "Phương thức")
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @LogField(name = "Mã giao dịch")
    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @LogField(name = "Trạng thái")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @LogField(name = "Thời điểm thanh toán")
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}