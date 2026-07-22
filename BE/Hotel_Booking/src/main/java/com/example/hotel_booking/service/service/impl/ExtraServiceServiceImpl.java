package com.example.hotel_booking.service.service.impl;

import com.example.hotel_booking.audit.service.AuditLogService;
import com.example.hotel_booking.common.ServiceType;
import com.example.hotel_booking.common.exception.AppException;
import com.example.hotel_booking.common.exception.ErrorCode;
import com.example.hotel_booking.service.dto.CreateExtraServiceRequest;
import com.example.hotel_booking.service.dto.ExtraServiceResponse;
import com.example.hotel_booking.service.dto.UpdateExtraServiceRequest;
import com.example.hotel_booking.service.entity.ExtraService;
import com.example.hotel_booking.service.mapper.ExtraServiceMapper;
import com.example.hotel_booking.service.repository.ExtraServiceRepository;
import com.example.hotel_booking.service.service.ExtraServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExtraServiceServiceImpl implements ExtraServiceService {
    private final ExtraServiceRepository extraServiceRepository;
    private final ExtraServiceMapper extraServiceMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public ExtraServiceResponse createService(CreateExtraServiceRequest request) {
        if (extraServiceRepository.findByNameIgnoreCase(request.getName()).isPresent()) {
            throw new AppException(ErrorCode.SERVICE_NAME_EXISTED);
        }

        ExtraService service = extraServiceMapper.toEntity(request);
        service.setActive(true); 
        service = extraServiceRepository.save(service);

        return extraServiceMapper.toResponse(service);
    }

    @Override
    @Transactional(readOnly = true)
    public ExtraServiceResponse getById(Long id) {
        return extraServiceRepository.findById(id)
                .map(extraServiceMapper::toResponse)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExtraServiceResponse> getAllServices(String name, Boolean active, ServiceType serviceType, Pageable pageable) {
        return extraServiceRepository.findServicesForAdmin(name, active, serviceType, pageable)
                .map(extraServiceMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExtraServiceResponse> getPublicServices(String name, Pageable pageable) {
        return extraServiceRepository.findPublicServices(name, pageable)
                .map(extraServiceMapper::toResponse);
    }

    @Override
    @Transactional
    public ExtraServiceResponse updateService(Long id, UpdateExtraServiceRequest request) {
        ExtraService service = extraServiceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        if (request.getName() != null && !request.getName().equalsIgnoreCase(service.getName())) {
            if (extraServiceRepository.findByNameIgnoreCase(request.getName()).isPresent()) {
                throw new AppException(ErrorCode.SERVICE_NAME_EXISTED);
            }
        }

        extraServiceMapper.updateEntity(service, request);
        return extraServiceMapper.toResponse(extraServiceRepository.save(service));
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        ExtraService service = extraServiceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
        service.setActive(false); 
        extraServiceRepository.save(service);
    }
}