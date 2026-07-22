package com.example.hotel_booking.customer.service.impl;

import com.example.hotel_booking.audit.annotation.LogAction;
import com.example.hotel_booking.audit.service.AuditLogService;
import com.example.hotel_booking.common.exception.AppException;
import com.example.hotel_booking.common.exception.ErrorCode;
import com.example.hotel_booking.customer.dto.CreateCustomerRequest;
import com.example.hotel_booking.customer.dto.CustomerResponse;
import com.example.hotel_booking.customer.dto.UpdateCustomerRequest;
import com.example.hotel_booking.customer.entity.Customer;
import com.example.hotel_booking.customer.mapper.CustomerMapper;
import com.example.hotel_booking.customer.repository.CustomerRepository;
import com.example.hotel_booking.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber)
                .map(customerMapper::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toResponse)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
    }

    @Override
    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }
        if (StringUtils.hasText(request.getIdentityCard())) {
            if (customerRepository.findByIdentityCard(request.getIdentityCard()).isPresent()) {
                throw new AppException(ErrorCode.IDENTITY_CARD_EXISTED);
            }
        }
        Customer customer = customerMapper.toEntity(request);
        customer.setActive(true);

        customer = customerRepository.save(customer);

        auditLogService.saveLog("CUSTOMER", "CREATE", customer.getId(),
                "Tạo mới khách hàng: " + customer.getFullName() + " (SĐT: " + customer.getPhoneNumber() + ")");

        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAllCustomers(String keyword, Boolean active, Pageable pageable) {

        
        if (StringUtils.hasText(keyword)) {
            return customerRepository.searchCustomers(active, keyword, pageable)
                    .map(customerMapper::toResponse);
        }

        
        if (active == null) {
            return customerRepository.findAll(pageable)
                    .map(customerMapper::toResponse);
        }

        
        return customerRepository.findByActive(active, pageable)
                .map(customerMapper::toResponse);
    }

    @Override
    @Transactional
    @LogAction(module = "CUSTOMER", action = "UPDATE", targetId = "#id", entityClass = Customer.class)
    public CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));

        if (!Objects.equals(customer.getPhoneNumber(), request.getPhoneNumber())) {
            if (customerRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent())
                throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }

        if (!Objects.equals(customer.getIdentityCard(), request.getIdentityCard()) && StringUtils.hasText(request.getIdentityCard())) {
            if (customerRepository.findByIdentityCard(request.getIdentityCard()).isPresent())
                throw new AppException(ErrorCode.IDENTITY_CARD_EXISTED);
        }

        customerMapper.updateEntity(customer, request);
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    
    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));

        
        boolean newStatus = !customer.getActive();
        customer.setActive(newStatus);
        customerRepository.save(customer);

        
        String action = newStatus ? "RESTORE" : "DELETE";
        String logMessage = newStatus ? "Khôi phục khách hàng: " : "Xóa mềm khách hàng: ";

        auditLogService.saveLog("CUSTOMER", action, id, logMessage + customer.getFullName());
    }

    @Override
    public long countNewCustomers(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new AppException(ErrorCode.INVALID_TIME_RANGE);
        }
        return customerRepository.countNewCustomersByTimeRange(startDate, endDate);
    }
}