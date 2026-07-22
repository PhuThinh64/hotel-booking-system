package com.example.hotel_booking.bookingservicedetail.entity;

import com.example.hotel_booking.audit.annotation.LogField;
import com.example.hotel_booking.audit.util.AuditDisplayable;
import com.example.hotel_booking.audit.util.HasParent;
import com.example.hotel_booking.audit.util.Identifiable;
import com.example.hotel_booking.booking.entity.Booking;
import com.example.hotel_booking.common.BookingServiceStatus;
import com.example.hotel_booking.service.entity.ExtraService;
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
@Table(name = "booking_services", indexes = {
        @Index(name = "idx_booking_services_booking", columnList = "booking_id"),
        @Index(name = "idx_booking_services_service", columnList = "service_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BookingServiceDetail implements HasParent, AuditDisplayable, Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ExtraService extraService;

    @LogField(name = "Số lượng dịch vụ")
    @Column(nullable = false)
    private Integer quantity;

    @LogField(name = "Đơn giá dịch vụ")
    @Column(name = "price_at_order", nullable = false)
    private BigDecimal priceAtOrder;

    @LogField(name = "Trạng thái dịch vụ")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private BookingServiceStatus status = BookingServiceStatus.ACTIVE;

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

    @Override
    public Long getParentId() {
        return (this.booking != null) ? this.booking.getId() : null;
    }

    @Override
    public String getAuditDisplayName() {
        String serviceName = (this.extraService != null && this.extraService.getName() != null)
                ? this.extraService.getName()
                : "Không xác định";
        return "dịch vụ [" + serviceName + "]";
    }
}