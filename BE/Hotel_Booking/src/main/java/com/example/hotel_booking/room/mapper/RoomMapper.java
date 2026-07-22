package com.example.hotel_booking.room.mapper;

import com.example.hotel_booking.room.dto.CreateRoomRequest;
import com.example.hotel_booking.room.dto.RoomResponse;
import com.example.hotel_booking.room.dto.UpdateRoomRequest;
import com.example.hotel_booking.room.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(source = "roomType.name", target = "roomType")
    @Mapping(source = "roomType.price", target = "price")
    RoomResponse toResponse(Room room);

    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "roomType", ignore = true)
    Room toEntity(CreateRoomRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "roomType", ignore = true)
    void updateRoomFromRequest(UpdateRoomRequest request, @MappingTarget Room room);
}