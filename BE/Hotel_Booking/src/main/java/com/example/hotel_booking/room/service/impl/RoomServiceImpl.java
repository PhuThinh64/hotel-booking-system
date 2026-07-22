package com.example.hotel_booking.room.service.impl;

import com.example.hotel_booking.audit.service.AuditLogService;
import com.example.hotel_booking.bookingroom.repository.BookingRoomRepository;
import com.example.hotel_booking.common.RoomStatus;
import com.example.hotel_booking.common.exception.AppException;
import com.example.hotel_booking.common.exception.ErrorCode;
import com.example.hotel_booking.room.dto.CreateRoomRequest;
import com.example.hotel_booking.room.dto.RoomResponse;
import com.example.hotel_booking.room.dto.UpdateRoomRequest;
import com.example.hotel_booking.room.entity.Room;
import com.example.hotel_booking.room.mapper.RoomMapper;
import com.example.hotel_booking.room.repository.RoomRepository;
import com.example.hotel_booking.room.service.RoomService;
import com.example.hotel_booking.roomtype.entity.RoomType;
import com.example.hotel_booking.roomtype.repository.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final BookingRoomRepository bookingRoomRepository;
    private final RoomMapper roomMapper;
    private final AuditLogService auditLogService;


    @Override
    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request) {
        
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new AppException(ErrorCode.ROOM_ALREADY_EXISTS);
        }

        
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        
        Room room = roomMapper.toEntity(request);

        
        room.setRoomType(roomType);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setActive(true); 

        room = roomRepository.save(room);

        
        auditLogService.saveLog("ROOM", "CREATE", room.getId(),
                "Tạo phòng mới: " + room.getRoomNumber() + " (Loại: " + roomType.getName() + ")");

        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional
    public void updateRoomStatus(Long roomId, RoomStatus status) {
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        RoomStatus oldStatus = room.getStatus();

        
        room.setStatus(status);
        roomRepository.save(room);

        
        auditLogService.saveLog("ROOM", "UPDATE_STATUS", roomId,
                String.format("Thay đổi trạng thái phòng [%s] từ %s sang %s", room.getRoomNumber(), oldStatus, status));
    }

    @Override
    @Transactional
    public RoomResponse updateRoom(Long id, UpdateRoomRequest request) {
        
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        
        if (!room.getRoomNumber().equals(request.getRoomNumber())) {
            if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
                throw new AppException(ErrorCode.ROOM_ALREADY_EXISTS);
            }
        }

        
        if (!room.getRoomType().getId().equals(request.getRoomTypeId())) {
            RoomType newRoomType = roomTypeRepository.findById(request.getRoomTypeId())
                    .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));
            room.setRoomType(newRoomType);
        }

        if (request.getStatus() != null) {
            if (room.getStatus() == RoomStatus.OCCUPIED ) {
                
            } else {
                room.setStatus(request.getStatus());
            }
        }

        
        roomMapper.updateRoomFromRequest(request, room);

        Room updatedRoom = roomRepository.save(room);

        return roomMapper.toResponse(updatedRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponse> getAllRooms(Pageable pageable, String roomNumber, RoomStatus status, Boolean active, Integer floor, Long roomTypeId) {
        String searchNumber = (roomNumber != null && !roomNumber.isEmpty()) ? roomNumber : null;

        return roomRepository.findWithFilter(searchNumber, status, active, floor, roomTypeId, pageable)
                .map(roomMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoom(Long id) {
        return roomRepository.findById(id)
                .map(roomMapper::toResponse)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        if (Boolean.TRUE.equals(room.getActive())) {
            room.setActive(false);
        } else {
            room.setActive(true);
        }

        roomRepository.save(room);

    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getAvailableRooms(
            Long roomTypeId,
            LocalDateTime checkIn,
            LocalDateTime checkOut
    ) {

        if (checkIn.isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.CHECKIN_DATE_PAST);
        }

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }

        List<Room> rooms =
                roomRepository.findAvailableRooms(roomTypeId, checkIn, checkOut);

        return rooms.stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void confirmCleaned(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        if (room.getStatus() != RoomStatus.CLEANING) {
            throw new AppException(ErrorCode.INVALID_ROOM_STATUS);
        }

        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);
        auditLogService.saveLog("ROOM", "CLEANING_COMPLETED", roomId,
                "Xác nhận dọn sạch phòng: " + room.getRoomNumber());
    }

    @Override
    public List<Integer> getDistinctFloors() {
        return roomRepository.findDistinctFloors();
    }


}