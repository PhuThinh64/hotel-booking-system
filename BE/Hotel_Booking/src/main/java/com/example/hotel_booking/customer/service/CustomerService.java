package com.example.hotel_booking.customer.service;

import com.example.hotel_booking.customer.dto.CreateCustomerRequest;
import com.example.hotel_booking.customer.dto.CustomerResponse;
import com.example.hotel_booking.customer.dto.UpdateCustomerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

public interface CustomerService {
    CustomerResponse getByPhoneNumber(String phoneNumber);
    CustomerResponse getById(Long id);
    CustomerResponse createCustomer(CreateCustomerRequest request);
    Page<CustomerResponse> getAllCustomers(String keyword, Boolean active, Pageable pageable);
    CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request);

    void deleteCustomer(Long id);

    long countNewCustomers(LocalDateTime startDate, LocalDateTime endDate);
}