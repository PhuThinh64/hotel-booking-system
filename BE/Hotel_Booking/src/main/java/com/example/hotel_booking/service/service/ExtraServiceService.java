package com.example.hotel_booking.service.service;

import com.example.hotel_booking.common.ServiceType;
import com.example.hotel_booking.service.dto.CreateExtraServiceRequest;
import com.example.hotel_booking.service.dto.ExtraServiceResponse;
import com.example.hotel_booking.service.dto.UpdateExtraServiceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExtraServiceService {
    ExtraServiceResponse createService(CreateExtraServiceRequest request);
    ExtraServiceResponse getById(Long id);

    
    Page<ExtraServiceResponse> getAllServices(String name, Boolean active, ServiceType serviceType, Pageable pageable);

    Page<ExtraServiceResponse> getPublicServices(String name, Pageable pageable);
    ExtraServiceResponse updateService(Long id, UpdateExtraServiceRequest request);
    void deleteService(Long id);
}