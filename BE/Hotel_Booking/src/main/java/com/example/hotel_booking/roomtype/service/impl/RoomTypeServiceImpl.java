package com.example.hotel_booking.roomtype.service.impl;

import com.example.hotel_booking.audit.service.AuditLogService;
import com.example.hotel_booking.bookingroom.repository.BookingRoomRepository;
import com.example.hotel_booking.common.exception.AppException;
import com.example.hotel_booking.common.exception.ErrorCode;
import com.example.hotel_booking.room.repository.RoomRepository;
import com.example.hotel_booking.roomtype.dto.CreateRoomType;
import com.example.hotel_booking.roomtype.dto.RoomTypeResponse;
import com.example.hotel_booking.roomtype.dto.UpdateRoomType;
import com.example.hotel_booking.roomtype.entity.RoomType;
import com.example.hotel_booking.roomtype.mapper.RoomTypeMapper;
import com.example.hotel_booking.roomtype.repository.RoomTypeRepository;
import com.example.hotel_booking.roomtype.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final BookingRoomRepository bookingRoomRepository;
    private final RoomTypeMapper roomTypeMapper;
    private final AuditLogService auditLogService;

    
    @Override
    public long getAvailableCount(Long roomTypeId, LocalDateTime checkIn, LocalDateTime checkOut, Long excludeBookingId) {
        long totalRooms = roomRepository.countByRoomTypeIdAndActiveTrue(roomTypeId);

        
        long bookedRooms = (excludeBookingId == null)
                ? bookingRoomRepository.countBookedByRoomType(roomTypeId, checkIn, checkOut)
                : bookingRoomRepository.countBookedByRoomTypeExcludeCurrent(roomTypeId, checkIn, checkOut, excludeBookingId);

        return Math.max(0, totalRooms - bookedRooms);
    }

    
    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeResponse> getAvailableRoomTypesWithCount(LocalDateTime checkIn, LocalDateTime checkOut) {
        
        return roomTypeRepository.findAll().stream()
                .filter(RoomType::getActive) 
                .map(type -> {
                    
                    long available = getAvailableCount(type.getId(), checkIn, checkOut, null);

                    RoomTypeResponse res = roomTypeMapper.RoomTypeResponse(type);
                    res.setAvailableCount(available);
                    return res;
                }).toList();
    }

    @Override
    public Page<RoomTypeResponse> getAllRoomType(Boolean active, String name, Pageable pageable) {
        return roomTypeRepository.findAllWithFilters(active, name, pageable)
                .map(roomTypeMapper::RoomTypeResponse);
    }

    @Override
    public RoomTypeResponse getRoomTypeById(Long id) {
        return roomTypeRepository.findById(id)
                .map(roomTypeMapper::RoomTypeResponse)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));
    }

    @Override
    @Transactional
    public RoomTypeResponse createRoomType(CreateRoomType request) {

        if (roomTypeRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.ROOM_TYPE_ALREADY_EXISTS);
        }

        RoomType roomType = roomTypeMapper.toEntity(request);
        roomType = roomTypeRepository.save(roomType);

        auditLogService.saveLog("ROOM_TYPE", "CREATE", roomType.getId(),
                "Tạo mới loại phòng: " + roomType.getName() + " với giá: " + roomType.getPrice());

        return roomTypeMapper.RoomTypeResponse(roomType);
    }

    @Override
    @Transactional
    public RoomTypeResponse updateRoomType(Long id, UpdateRoomType request) {

        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        if (!roomType.getName().equalsIgnoreCase(request.getName())) {
            if (roomTypeRepository.existsByName(request.getName())) {
                throw new AppException(ErrorCode.ROOM_TYPE_ALREADY_EXISTS);
            }
        }

        roomTypeMapper.updateRoomType(roomType, request);
        roomType = roomTypeRepository.save(roomType);

        auditLogService.saveLog("ROOM_TYPE", "UPDATE", id,
                "Cập nhật loại phòng [" + roomType.getName() + "]. Giá mới: " + roomType.getPrice());

        return roomTypeMapper.RoomTypeResponse(roomType);
    }

    @Override
    @Transactional
    public void deleteRoomType(Long id) {

        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        long activeRoomsCount = roomRepository.countByRoomTypeIdAndActiveTrue(id);
        if (activeRoomsCount > 0 && roomType.getActive()) {
            throw new AppException(ErrorCode.ROOM_TYPE_IN_USE);
        }

        roomType.setActive(!roomType.getActive());
        roomTypeRepository.save(roomType);

    }
}