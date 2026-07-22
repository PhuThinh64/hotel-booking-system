package com.example.hotel_booking.service.controller;

import com.example.hotel_booking.common.ServiceType;
import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.service.dto.CreateExtraServiceRequest;
import com.example.hotel_booking.service.dto.ExtraServiceResponse;
import com.example.hotel_booking.service.dto.UpdateExtraServiceRequest;
import com.example.hotel_booking.service.service.ExtraServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ExtraServiceController {
    private final ExtraServiceService extraServiceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<ExtraServiceResponse> create(@RequestBody @Valid CreateExtraServiceRequest request) {
        return ApiResponse.<ExtraServiceResponse>builder()
                .result(extraServiceService.createService(request))
                .build();
    }

    @GetMapping
    public ApiResponse<Page<ExtraServiceResponse>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) ServiceType serviceType,
            Pageable pageable) {
        return ApiResponse.<Page<ExtraServiceResponse>>builder()
                .result(extraServiceService.getAllServices(name, active, serviceType, pageable)) 
                .build();
    }

    @GetMapping("/public")
    public ApiResponse<Page<ExtraServiceResponse>> getPublicServices(
            @RequestParam(required = false) String name,
            Pageable pageable) {
        return ApiResponse.<Page<ExtraServiceResponse>>builder()
                .result(extraServiceService.getPublicServices(name, pageable))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ExtraServiceResponse> getById(@PathVariable Long id) {
        return ApiResponse.<ExtraServiceResponse>builder()
                .result(extraServiceService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<ExtraServiceResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateExtraServiceRequest request) {
        return ApiResponse.<ExtraServiceResponse>builder()
                .result(extraServiceService.updateService(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        extraServiceService.deleteService(id);
        return ApiResponse.<Void>builder()
                .message("Service deactivated successfully")
                .build();
    }
}