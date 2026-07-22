package com.example.hotel_booking.employee.entity;

import com.example.hotel_booking.audit.annotation.LogField;
import com.example.hotel_booking.common.entity.BaseEntity;
import com.example.hotel_booking.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user; 

    @LogField(name = "Họ tên")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @LogField(name = "Số điện thoại")
    @Column(name = "phone_number")
    private String phoneNumber;

    @LogField(name = "Email")
    @Column(name = "email")
    private String email;


}