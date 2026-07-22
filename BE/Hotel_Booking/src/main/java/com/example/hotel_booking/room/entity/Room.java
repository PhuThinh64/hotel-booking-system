package com.example.hotel_booking.room.entity;

import com.example.hotel_booking.audit.annotation.LogField;
import com.example.hotel_booking.audit.util.AuditDisplayable;
import com.example.hotel_booking.audit.util.Identifiable;
import com.example.hotel_booking.common.RoomStatus;
import com.example.hotel_booking.common.entity.BaseEntity;
import com.example.hotel_booking.roomtype.entity.RoomType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "rooms", indexes = {
      @Index(name = "idx_rooms_type_status", columnList = "room_type_id, status")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Room extends BaseEntity implements AuditDisplayable, Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @LogField(name = "Số phòng")
    @Column(name="room_number", length=10, unique=true)
    private String roomNumber;

    @LogField(name = "Tầng")
    private Integer floor;

    @LogField(name = "Trạng thái")
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @LogField(name = "Loại phòng")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @Override
    public String getAuditDisplayName() {
        
        return "Phòng " + this.getRoomNumber() + " (ID: " + this.getId() + ")";
    }
}
