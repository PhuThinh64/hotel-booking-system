package com.example.hotel_booking.bookingservicedetail.mapper;

import com.example.hotel_booking.booking.entity.Booking;
import com.example.hotel_booking.bookingservicedetail.dto.BookingServiceCreateRequest;
import com.example.hotel_booking.bookingservicedetail.dto.BookingServiceDetailResponse;
import com.example.hotel_booking.bookingservicedetail.entity.BookingServiceDetail;
import com.example.hotel_booking.service.entity.ExtraService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",imports = {java.math.BigDecimal.class})
public interface BookingServiceDetailMapper {

    @Mapping(source = "booking.id", target = "bookingId")
    @Mapping(source = "extraService.name", target = "serviceName")
    @Mapping(source = "extraService.serviceType", target = "serviceType")
    @Mapping(target = "totalPrice", expression = "java(entity.getPriceAtOrder().multiply(BigDecimal.valueOf(entity.getQuantity())))")
    BookingServiceDetailResponse toResponse(BookingServiceDetail entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", source = "booking")
    @Mapping(target = "extraService", source = "service")
    @Mapping(target = "priceAtOrder", source = "service.price")
    @Mapping(target = "quantity", source = "request.quantity")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    BookingServiceDetail toEntity(BookingServiceCreateRequest request, Booking booking, ExtraService service);
}