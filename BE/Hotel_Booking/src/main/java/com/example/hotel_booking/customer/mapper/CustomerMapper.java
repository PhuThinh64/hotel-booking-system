package com.example.hotel_booking.customer.mapper;

import com.example.hotel_booking.customer.dto.CreateCustomerRequest;
import com.example.hotel_booking.customer.dto.CustomerResponse;
import com.example.hotel_booking.customer.dto.UpdateCustomerRequest;
import com.example.hotel_booking.customer.entity.Customer;
import com.example.hotel_booking.user.dto.CustomerProfileUpdateRequest;
import com.example.hotel_booking.user.dto.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", ignore = true)
    Customer toEntity(CreateCustomerRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", constant = "true")
    Customer toEntityFromRegister(RegisterRequest request);

    CustomerResponse toResponse(Customer entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntity(@MappingTarget Customer entity, UpdateCustomerRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(source = "phone", target = "phoneNumber")
    void updateEntityFromProfile(@MappingTarget Customer entity, CustomerProfileUpdateRequest request);
}