package com.example.hotel_booking.room.controller;

import com.example.hotel_booking.common.RoomStatus;
import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.common.service.FileService;
import com.example.hotel_booking.config.swagger.constants.ApiInfoConstants;
import com.example.hotel_booking.config.swagger.constants.ParameterDescriptions;
import com.example.hotel_booking.config.swagger.constants.SwaggerResponseMessages;
import com.example.hotel_booking.config.swagger.constants.SwaggerTags;
import com.example.hotel_booking.room.dto.CreateRoomRequest;
import com.example.hotel_booking.room.dto.RoomResponse;
import com.example.hotel_booking.room.dto.UpdateRoomRequest;
import com.example.hotel_booking.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = SwaggerTags.ROOM,
        description = "Manage hotel rooms, including creation, retrieval, updates, status changes, availability checking, and deletion."
)
@SecurityRequirement(name = ApiInfoConstants.SECURITY_SCHEME)
public class RoomController {

    private final RoomService roomService;
    private final FileService fileService;

    @Operation(
            summary = "Create a New Room",
            description = "Create a new room in the system by specifying room number, room type, and floor."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.CREATED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = SwaggerResponseMessages.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = SwaggerResponseMessages.FORBIDDEN
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = SwaggerResponseMessages.CONFLICT
            )
    })
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public ApiResponse<RoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.createRoom(request))
                .build();
    }

    @Operation(
            summary = "Retrieve Room List",
            description = "Retrieve a paginated list of rooms with optional filtering by room number, status, active status, floor, and room type.",
            security = {}
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = SwaggerResponseMessages.UNAUTHORIZED
            )
    })
    @GetMapping
    public ApiResponse<Page<RoomResponse>> getRooms(
            @PageableDefault(size = 10, sort = "roomNumber") Pageable pageable,

            @Parameter(
                    description = "Room number to filter by.",
                    example = "101"
            )
            @RequestParam(required = false)
            String roomNumber,

            @Parameter(
                    description = "Room status to filter by.",
                    example = "AVAILABLE"
            )
            @RequestParam(required = false)
            RoomStatus status,

            @Parameter(
                    description = "Active status filter.",
                    example = "true"
            )
            @RequestParam(required = false)
            Boolean active,

            @Parameter(
                    description = "Floor number to filter by.",
                    example = "1"
            )
            @RequestParam(required = false)
            Integer floor,

            @Parameter(
                    description = ParameterDescriptions.ROOM_TYPE_ID,
                    example = "3"
            )
            @RequestParam(required = false)
            Long roomTypeId
    ) {
        return ApiResponse.<Page<RoomResponse>>builder()
                .result(roomService.getAllRooms(pageable, roomNumber, status, active, floor, roomTypeId))
                .build();
    }

    @Operation(
            summary = "Retrieve Room Details",
            description = "Retrieve detailed information of a specific room by its identifier.",
            security = {}
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            )
    })
    @GetMapping("/{id}")
    public ApiResponse<RoomResponse> getRoom(
            @Parameter(
                    description = ParameterDescriptions.ROOM_ID,
                    example = "5"
            )
            @PathVariable
            Long id
    ) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.getRoom(id))
                .build();
    }

    @Operation(
            summary = "Update Room Status",
            description = "Update the operational status of a specific room."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            )
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @PutMapping("/{roomId}/status")
    public ApiResponse<String> updateRoomStatus(
            @Parameter(
                    description = ParameterDescriptions.ROOM_ID,
                    example = "5"
            )
            @PathVariable
            Long roomId,

            @Parameter(
                    description = "New status to apply to the room.",
                    example = "CLEANING"
            )
            @RequestParam
            RoomStatus status
    ) {
        roomService.updateRoomStatus(roomId, status);

        return ApiResponse.<String>builder()
                .result("Cập nhật trạng thái phòng thành công!")
                .build();
    }

    @Operation(
            summary = "Update Room Details",
            description = "Update room number, type, floor, or status of an existing room."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = SwaggerResponseMessages.CONFLICT
            )
    })
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<RoomResponse> updateRoom(
            @Parameter(
                    description = ParameterDescriptions.ROOM_ID,
                    example = "5"
            )
            @PathVariable
            Long id,

            @Valid
            @RequestBody
            UpdateRoomRequest request
    ) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.updateRoom(id, request))
                .build();
    }

    @Operation(
            summary = "Delete Room",
            description = "Delete a specific room from the system by its identifier."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.DELETED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = SwaggerResponseMessages.CONFLICT
            )
    })
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRoom(
            @Parameter(
                    description = ParameterDescriptions.ROOM_ID,
                    example = "5"
            )
            @PathVariable
            Long id
    ) {
        roomService.deleteRoom(id);
        return ApiResponse.<Void>builder()
                .message("Room deleted successfully")
                .build();
    }

    @Operation(
            summary = "Retrieve Available Rooms",
            description = "Get a list of available rooms for a specific room type and date range.",
            security = {}
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            )
    })
    @GetMapping("/available")
    public ApiResponse<List<RoomResponse>> getAvailableRooms(
            @Parameter(
                    description = ParameterDescriptions.ROOM_TYPE_ID,
                    example = "3"
            )
            @RequestParam
            Long roomTypeId,

            @Parameter(
                    description = "Target check-in date and time.",
                    example = "2026-08-15T14:00:00"
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime checkIn,

            @Parameter(
                    description = "Target check-out date and time.",
                    example = "2026-08-17T12:00:00"
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime checkOut
    ) {
        return ApiResponse.<List<RoomResponse>>builder()
                .result(roomService.getAvailableRooms(roomTypeId, checkIn, checkOut))
                .build();
    }

    @Operation(
            summary = "Confirm Room Cleaned",
            description = "Confirm that a room has been cleaned and is ready for guests.",
            security = {}
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            )
    })
    @PostMapping("/{roomId}/confirm-cleaned")
    public ApiResponse<String> confirmCleaned(
            @Parameter(
                    description = ParameterDescriptions.ROOM_ID,
                    example = "5"
            )
            @PathVariable
            Long roomId
    ) {
        roomService.confirmCleaned(roomId);
        return ApiResponse.<String>builder()
                .result("Phòng đã dọn dẹp xong, sẵn sàng đón khách!")
                .build();
    }

    @Operation(
            summary = "Retrieve Distinct Floors",
            description = "Retrieve a list of all distinct floor numbers in the hotel.",
            security = {}
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            )
    })
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