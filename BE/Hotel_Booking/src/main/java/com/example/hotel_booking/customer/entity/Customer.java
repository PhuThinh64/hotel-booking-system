package com.example.hotel_booking.customer.entity;

import com.example.hotel_booking.audit.annotation.LogField;
import com.example.hotel_booking.common.Gender;
import com.example.hotel_booking.common.entity.BaseEntity;
import com.example.hotel_booking.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customers_user", columnList = "user_id"),
        @Index(name = "idx_customers_fullname", columnList = "full_name")
})
@Getter @Setter @SuperBuilder
@NoArgsConstructor @AllArgsConstructor
public class Customer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @LogField(name = "Họ tên")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @LogField(name = "Số điện thoại")
    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @LogField(name = "Số CCCD/Passport")
    @Column(name = "identity_card", unique = true, length = 20)
    private String identityCard;

    @LogField(name = "Email")
    private String email;

    @LogField(name = "Giới tính")
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @LogField(name = "Quốc tịch")
    private String nationality;

    @LogField(name = "Địa chỉ")
    private String address;

    @LogField(name = "Ngày sinh")
    private LocalDate birthday;
}