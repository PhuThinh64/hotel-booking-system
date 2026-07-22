package com.example.hotel_booking.service.entity;

import com.example.hotel_booking.audit.annotation.LogField;
import com.example.hotel_booking.common.ServiceType;
import com.example.hotel_booking.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "services")
@Getter @Setter @SuperBuilder
@NoArgsConstructor @AllArgsConstructor
public class ExtraService extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @LogField(name = "Tên dịch vụ")
    @Column(name="service_name", nullable = false, unique = true)
    private String name;

    @LogField(name = "Giá tiền")
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    @LogField(name = "Mô tả")
    private String description;

    @LogField(name = "Loại dịch vụ")
    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    private ServiceType serviceType = ServiceType.REGULAR;

}