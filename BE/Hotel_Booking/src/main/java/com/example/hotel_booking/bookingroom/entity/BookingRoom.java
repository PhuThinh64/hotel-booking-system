package com.example.hotel_booking.bookingroom.entity;

import com.example.hotel_booking.audit.annotation.LogField;
import com.example.hotel_booking.audit.util.AuditDisplayable;
import com.example.hotel_booking.audit.util.HasParent;
import com.example.hotel_booking.audit.util.Identifiable;
import com.example.hotel_booking.booking.entity.Booking;
import com.example.hotel_booking.common.BookingRoomStatus;
import com.example.hotel_booking.room.entity.Room;
import com.example.hotel_booking.roomtype.entity.RoomType;
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
@Table(name = "booking_rooms", indexes = {
        @Index(name = "idx_booking_rooms_booking", columnList = "booking_id"),
        @Index(name = "idx_booking_rooms_room", columnList = "room_id"),
        @Index(name = "idx_booking_rooms_room_type", columnList = "room_type_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BookingRoom implements HasParent, AuditDisplayable, Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @LogField(name = "Phòng cụ thể")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @LogField(name = "Đơn giá phòng")
    @Column(name = "price_at_order", nullable = false)
    private BigDecimal priceAtOrder;

    @LogField(name = "Lý do hủy")
    @Column(name="cancel_reason")
    private String cancelReason;

    @LogField(name = "Trạng thái phòng trong đơn")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private BookingRoomStatus status = BookingRoomStatus.ACTIVE;

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
        String roomName = (this.room != null && this.room.getRoomNumber() != null)
                ? this.room.getRoomNumber()
                : "Chưa xếp phòng";

        return "phòng [" + roomName + "]";
    }

}