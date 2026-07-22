package com.example.hotel_booking.booking.entity;

import com.example.hotel_booking.audit.annotation.LogField;
import com.example.hotel_booking.bookingroom.entity.BookingRoom;
import com.example.hotel_booking.bookingservicedetail.entity.BookingServiceDetail;
import com.example.hotel_booking.common.BookingStatus;
import com.example.hotel_booking.customer.entity.Customer;
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
import java.util.List;

@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_bookings_customer", columnList = "customer_id"),
        @Index(name = "idx_bookings_dates", columnList = "arrival_date, departure_date"),
        @Index(name = "idx_bookings_status_expiry", columnList = "status, expiry_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Booking   {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @LogField(name = "Tên người liên hệ")
    @Column(name = "contact_name", nullable = false)
    private String contactName;

    @LogField(name = "Số điện thoại liên hệ")
    @Column(name = "contact_phone", nullable = false)
    private String contactPhone;

    @LogField(name = "Trạng thái đặt phòng")
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BookingStatus status;

    @LogField(name = "Tiền phòng")
    @Column(name = "room_amount")
    private BigDecimal roomAmount;

    @LogField(name = "Tiền dịch vụ")
    @Column(name = "service_amount")
    private BigDecimal serviceAmount;

    @LogField(name = "Tổng tiền đơn")
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @LogField(name = "Tiền đặt cọc")
    @Column(name = "deposit_amount")
    private BigDecimal depositAmount;

    @LogField(name = "Tiền phụ thu")
    @Column(name = "surcharge_amount")
    private BigDecimal surchargeAmount = BigDecimal.ZERO;

    @LogField(name = "Thời hạn giữ phòng")
    @Column(name="expiry_date")
    private LocalDateTime expiryDate;

    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingRoom> bookingRooms;

    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingServiceDetail> bookingServices;

    @LogField(name = "Ngày đến dự kiến")
    @Column(name = "arrival_date")
    private LocalDateTime arrivalDate;

    @LogField(name = "Ngày đi dự kiến")
    @Column(name = "departure_date")
    private LocalDateTime departureDate;

    @LogField(name = "Thời gian Check-in thực tế")
    @Column(name = "actual_check_in")
    private LocalDateTime actualCheckIn;

    @LogField(name = "Thời gian Check-out thực tế")
    @Column(name = "actual_check_out")
    private LocalDateTime actualCheckOut;

    @LogField(name = "Mã đặt phòng")
    @Column(name = "booking_code", unique = true)
    private String bookingCode;

    @LogField(name = "Lý do hủy đơn")
    @Column(name="cancel_reason")
    private String cancelReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}