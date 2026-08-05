package com.example.hotel_booking.room.service;

import com.example.hotel_booking.common.RoomStatus;
import com.example.hotel_booking.room.dto.CreateRoomRequest;
import com.example.hotel_booking.room.dto.RoomResponse;
import com.example.hotel_booking.room.dto.UpdateRoomRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomService {

    RoomResponse createRoom(CreateRoomRequest request);

    Page<RoomResponse> getAllRooms(Pageable pageable, String roomNumber, RoomStatus status, Boolean active, Integer floor, Long roomTypeId);

    RoomResponse getRoom(Long id);

    void updateRoomStatus(Long roomId, RoomStatus status);

    RoomResponse updateRoom(Long id, UpdateRoomRequest request);

    void deleteRoom(Long id);


    void confirmCleaned(Long roomId);

    List<Integer> getDistinctFloors();

}