package com.example.hotel_booking.roomtype.controller;

import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.common.service.FileService;
import com.example.hotel_booking.roomtype.dto.CreateRoomType;
import com.example.hotel_booking.roomtype.dto.RoomTypeResponse;
import com.example.hotel_booking.roomtype.dto.UpdateRoomType;
import com.example.hotel_booking.roomtype.service.RoomTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/roomtype")
@RequiredArgsConstructor
public class RoomTypeController {
    private final RoomTypeService roomTypeService;
    private final FileService fileService;


    @GetMapping("/available-with-count")
    public ApiResponse<List<RoomTypeResponse>> getAvailableWithCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOut) {
        return ApiResponse.<List<RoomTypeResponse>>builder()
                .result(roomTypeService.getAvailableRoomTypesWithCount(checkIn, checkOut))
                .build();
    }

    @GetMapping
    public ApiResponse<Page<RoomTypeResponse>> getAllRoomTypes(
            @RequestParam(required = false, defaultValue = "true") Boolean active,
            @RequestParam(required = false) String name,
            Pageable pageable) {

        return ApiResponse.<Page<RoomTypeResponse>>builder()
                .result(roomTypeService.getAllRoomType(active, name, pageable))
                .build();
    }

    @GetMapping("/{roomTypeId}")
    public ApiResponse<RoomTypeResponse> getRoomTypeById(@PathVariable Long roomTypeId) {
    return ApiResponse.<RoomTypeResponse>builder()
                .result(roomTypeService.getRoomTypeById(roomTypeId))
                .build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<RoomTypeResponse> createRoomType(@RequestBody @Valid CreateRoomType request) {
        return ApiResponse.<RoomTypeResponse>builder()
                .result(roomTypeService.createRoomType(request))
                .build();
    }

    @PutMapping("/{roomTypeId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<RoomTypeResponse>  updateRoomType(@PathVariable Long roomTypeId, @RequestBody @Valid UpdateRoomType request) {
        return ApiResponse.<RoomTypeResponse>builder()
                .result(roomTypeService.updateRoomType(roomTypeId,request))
                .build();
    }

    @DeleteMapping("/{roomTypeId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Void> deleteRoomType(@PathVariable Long roomTypeId) {
        roomTypeService.deleteRoomType(roomTypeId);
        return ApiResponse.<Void>builder()
                .message("Room Type has been deleted")
                .build();
    }
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String filePath = fileService.saveFile(file);
        return ApiResponse.<String>builder()
                .result(filePath)
                .build();
    }
}
