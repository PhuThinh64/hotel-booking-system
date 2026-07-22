package com.example.hotel_booking.employee.mapper;

import com.example.hotel_booking.employee.dto.EmployeeResponse;
import com.example.hotel_booking.employee.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.role.name", target = "roleName")
    EmployeeResponse toResponse(Employee entity);
}