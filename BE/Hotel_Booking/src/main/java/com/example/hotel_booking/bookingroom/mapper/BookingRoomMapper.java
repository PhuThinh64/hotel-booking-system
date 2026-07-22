package com.example.hotel_booking.bookingroom.mapper;

import com.example.hotel_booking.booking.entity.Booking;
import com.example.hotel_booking.bookingroom.dto.BookingRoomCreateRequest;
import com.example.hotel_booking.bookingroom.dto.BookingRoomResponse;
import com.example.hotel_booking.bookingroom.dto.BookingRoomUpdateRequest;
import com.example.hotel_booking.bookingroom.entity.BookingRoom;
import com.example.hotel_booking.room.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", imports = {java.math.BigDecimal.class})
public interface BookingRoomMapper {

    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "room.roomNumber", target = "roomNumber")
    @Mapping(source = "entity.roomType.name", target = "roomType")
    @Mapping(source = "entity.id",target="bookingRoomId")
    @Mapping(target = "status", ignore = true)
    BookingRoomResponse toResponse(BookingRoom entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "booking", source = "booking")
    @Mapping(target = "room", source = "room")
    @Mapping(target = "priceAtOrder", source = "room.roomType.price")
    @Mapping(target = "status", ignore = true)
    BookingRoom toEntity(BookingRoomCreateRequest request, Booking booking, Room room);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "priceAtOrder", ignore = true)
    void updateEntityFromRequest(BookingRoomUpdateRequest request, @MappingTarget BookingRoom entity);
}