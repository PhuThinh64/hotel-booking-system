package com.example.hotel_booking.service.mapper;

import com.example.hotel_booking.service.dto.CreateExtraServiceRequest;
import com.example.hotel_booking.service.dto.UpdateExtraServiceRequest;
import com.example.hotel_booking.service.dto.ExtraServiceResponse;
import com.example.hotel_booking.service.entity.ExtraService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ExtraServiceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    ExtraService toEntity(CreateExtraServiceRequest request);

    ExtraServiceResponse toResponse(ExtraService entity);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget ExtraService entity, UpdateExtraServiceRequest request);
}