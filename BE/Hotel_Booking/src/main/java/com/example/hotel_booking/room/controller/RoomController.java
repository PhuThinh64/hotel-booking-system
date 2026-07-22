package com.example.hotel_booking.room.controller;

import com.example.hotel_booking.common.RoomStatus;
import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.room.dto.CreateRoomRequest;
import com.example.hotel_booking.room.dto.RoomResponse;
import com.example.hotel_booking.room.dto.UpdateRoomRequest;
import com.example.hotel_booking.common.service.FileService;
import com.example.hotel_booking.room.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final FileService fileService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<RoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.createRoom(request))
                .build();
    }

    @GetMapping
    public ApiResponse<Page<RoomResponse>> getRooms(
            @PageableDefault(size = 10, sort = "roomNumber") Pageable pageable,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) RoomStatus status,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) Long roomTypeId
    ) {
        return ApiResponse.<Page<RoomResponse>>builder()
                .result(roomService.getAllRooms(pageable, roomNumber, status, active, floor, roomTypeId))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<RoomResponse> getRoom(@PathVariable Long id) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.getRoom(id))
                .build();
    }

    @PutMapping("/{roomId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ApiResponse<String> updateRoomStatus(
            @PathVariable Long roomId,
            @RequestParam RoomStatus status) {

        roomService.updateRoomStatus(roomId, status);

        return ApiResponse.<String>builder()
                .result("Cập nhật trạng thái phòng thành công!")
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<RoomResponse> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoomRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.updateRoom(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ApiResponse.<Void>builder()
                .message("Room deleted successfully")
                .build();
    }

    @GetMapping("/available")
    public ApiResponse<List<RoomResponse>> getAvailableRooms(
            @RequestParam Long roomTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOut) {

        return ApiResponse.<List<RoomResponse>>builder()
                .result(roomService.getAvailableRooms(roomTypeId, checkIn, checkOut))
                .build();
    }
    @PostMapping("/{roomId}/confirm-cleaned")
    public ApiResponse<String> confirmCleaned(@PathVariable Long roomId) {
        roomService.confirmCleaned(roomId); 
        return ApiResponse.<String>builder()
                .result("Phòng đã dọn dẹp xong, sẵn sàng đón khách!")
                .build();
    }
    @GetMapping("/floors")
    public ApiResponse<List<Integer>> getDistinctFloors() {
        List<Integer> floors = roomService.getDistinctFloors();

        return ApiResponse.<List<Integer>>builder()
                .code(200)
                .message("Tải danh sách số tầng từ hệ thống thành công!")
                .result(floors)
                .build();
    }


}