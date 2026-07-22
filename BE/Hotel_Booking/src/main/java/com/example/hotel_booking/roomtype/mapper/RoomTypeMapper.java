package com.example.hotel_booking.roomtype.mapper;

import com.example.hotel_booking.roomtype.dto.CreateRoomType;
import com.example.hotel_booking.roomtype.dto.RoomTypeResponse;
import com.example.hotel_booking.roomtype.dto.UpdateRoomType;
import com.example.hotel_booking.roomtype.entity.RoomType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomTypeMapper {
    RoomType toEntity(CreateRoomType createRoomType);

    RoomTypeResponse RoomTypeResponse(RoomType roomType);

    void updateRoomType(@MappingTarget RoomType roomType, UpdateRoomType request);
}
