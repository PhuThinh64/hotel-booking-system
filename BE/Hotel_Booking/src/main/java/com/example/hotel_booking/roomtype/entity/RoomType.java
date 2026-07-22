package com.example.hotel_booking.roomtype.entity;

import com.example.hotel_booking.audit.annotation.LogField;
import com.example.hotel_booking.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "room_types")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @LogField(name = "Tên loại phòng")
    private String name;

    @LogField(name = "Giá")
    private BigDecimal price;

    @LogField(name = "Số khách tối đa")
    @Column(name="max_guest")
    private Integer maxGuest;

    @LogField(name = "Mô tả")
    private String description;

    @Column(name="image_url")
    private String imageUrl;
}