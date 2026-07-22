package com.example.hotel_booking.roomtype.service;

import com.example.hotel_booking.roomtype.dto.CreateRoomType;
import com.example.hotel_booking.roomtype.dto.RoomTypeResponse;
import com.example.hotel_booking.roomtype.dto.UpdateRoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface RoomTypeService {
    long getAvailableCount(Long roomTypeId, LocalDateTime checkIn, LocalDateTime checkOut, Long excludeBookingId);
    List<RoomTypeResponse> getAvailableRoomTypesWithCount(LocalDateTime checkIn, LocalDateTime checkOut);

    Page<RoomTypeResponse> getAllRoomType(Boolean active, String name, Pageable pageable);    RoomTypeResponse getRoomTypeById(Long id);
    RoomTypeResponse createRoomType(CreateRoomType request);
    RoomTypeResponse updateRoomType(Long id , UpdateRoomType request);
    void deleteRoomType(Long id);
}
