package com.example.hotel_booking.user.mapper;

import com.example.hotel_booking.customer.entity.Customer;
import com.example.hotel_booking.employee.entity.Employee;
import com.example.hotel_booking.user.dto.CustomerProfileResponse;
import com.example.hotel_booking.user.dto.CustomerProfileUpdateRequest;
import com.example.hotel_booking.user.dto.EmployeeProfileResponse;
import com.example.hotel_booking.user.dto.EmployeeProfileUpdateRequest;
import com.example.hotel_booking.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // === 1. Lấy thông tin Profile (Đã tách DTO) ===

    // Map cho Khách hàng
    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "customer.fullName", target = "fullName")
    @Mapping(source = "customer.phoneNumber", target = "phone")
    @Mapping(source = "customer.email", target = "email")
    // Các trường dưới đây tự động map vào các field của CustomerProfileResponse (vì kế thừa Base)
    CustomerProfileResponse toCustomerProfile(User user, Customer customer);

    // Map cho Nhân viên (Không cần 'ignore' nữa vì class này không có các field đó)
    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "employee.fullName", target = "fullName")
    @Mapping(source = "employee.phoneNumber", target = "phone")
    @Mapping(source = "employee.email", target = "email")
    EmployeeProfileResponse toEmployeeProfile(User user, Employee employee);


    // === 2. Cập nhật thông tin Profile (Giữ nguyên) ===

    @Mapping(source = "phone", target = "phoneNumber")
    void updateCustomerFromRequest(CustomerProfileUpdateRequest request, @MappingTarget Customer customer);

    // Update cho Employee
    @Mapping(source = "phone", target = "phoneNumber")
    void updateEmployeeFromRequest(EmployeeProfileUpdateRequest request, @MappingTarget Employee employee);

}