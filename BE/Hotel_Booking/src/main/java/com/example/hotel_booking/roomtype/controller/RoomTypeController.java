package com.example.hotel_booking.roomtype.controller;

import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.common.service.FileService;
import com.example.hotel_booking.config.swagger.constants.ApiInfoConstants;
import com.example.hotel_booking.config.swagger.constants.ParameterDescriptions;
import com.example.hotel_booking.config.swagger.constants.SwaggerResponseMessages;
import com.example.hotel_booking.config.swagger.constants.SwaggerTags;
import com.example.hotel_booking.roomtype.dto.CreateRoomType;
import com.example.hotel_booking.roomtype.dto.RoomTypeResponse;
import com.example.hotel_booking.roomtype.dto.UpdateRoomType;
import com.example.hotel_booking.roomtype.service.RoomTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = SwaggerTags.ROOM_TYPE,
        description = "Manage hotel room types, including creation, retrieval, updates, deletion, and availability counting."
)
@SecurityRequirement(name = ApiInfoConstants.SECURITY_SCHEME)
public class RoomTypeController {

    private final RoomTypeService roomTypeService;
    private final FileService fileService;

    @Operation(
            summary = "Retrieve Available Room Types with Count",
            description = "Get a list of room types with the count of available rooms for each type within the specified date range.",
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
    @GetMapping("/available-with-count")
    public ApiResponse<List<RoomTypeResponse>> getAvailableWithCount(
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
        return ApiResponse.<List<RoomTypeResponse>>builder()
                .result(roomTypeService.getAvailableRoomTypesWithCount(checkIn, checkOut))
                .build();
    }

    @Operation(
            summary = "Retrieve Room Type List",
            description = "Retrieve a paginated list of room types with optional filtering by name and active status.",
            security = {}
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            )
    })
    @GetMapping
    public ApiResponse<Page<RoomTypeResponse>> getAllRoomTypes(
            @Parameter(
                    description = "Filter by active state.",
                    example = "true"
            )
            @RequestParam(required = false, defaultValue = "true")
            Boolean active,

            @Parameter(
                    description = "Filter by room type name.",
                    example = "DELUXE"
            )
            @RequestParam(required = false)
            String name,

            Pageable pageable
    ) {
        return ApiResponse.<Page<RoomTypeResponse>>builder()
                .result(roomTypeService.getAllRoomType(active, name, pageable))
                .build();
    }

    @Operation(
            summary = "Retrieve Room Type Details",
            description = "Retrieve detailed information of a specific room type by its identifier.",
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
    @GetMapping("/{roomTypeId}")
    public ApiResponse<RoomTypeResponse> getRoomTypeById(
            @Parameter(
                    description = ParameterDescriptions.ROOM_TYPE_ID,
                    example = "3"
            )
            @PathVariable
            Long roomTypeId
    ) {
        return ApiResponse.<RoomTypeResponse>builder()
                .result(roomTypeService.getRoomTypeById(roomTypeId))
                .build();
    }

    @Operation(
            summary = "Create a New Room Type",
            description = "Create a new room type in the system by specifying name, price, max guest limit, description, and image URL."
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
    public ApiResponse<RoomTypeResponse> createRoomType(@RequestBody @Valid CreateRoomType request) {
        return ApiResponse.<RoomTypeResponse>builder()
                .result(roomTypeService.createRoomType(request))
                .build();
    }

    @Operation(
            summary = "Update Room Type Details",
            description = "Update details of an existing room type such as name, price, max guest limit, description, and image URL."
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
    @PutMapping("/{roomTypeId}")
    public ApiResponse<RoomTypeResponse> updateRoomType(
            @Parameter(
                    description = ParameterDescriptions.ROOM_TYPE_ID,
                    example = "3"
            )
            @PathVariable
            Long roomTypeId,

            @RequestBody
            @Valid
            UpdateRoomType request
    ) {
        return ApiResponse.<RoomTypeResponse>builder()
                .result(roomTypeService.updateRoomType(roomTypeId, request))
                .build();
    }

    @Operation(
            summary = "Delete Room Type",
            description = "Delete a specific room type from the system by its identifier."
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
    @DeleteMapping("/{roomTypeId}")
    public ApiResponse<Void> deleteRoomType(
            @Parameter(
                    description = ParameterDescriptions.ROOM_TYPE_ID,
                    example = "3"
            )
            @PathVariable
            Long roomTypeId
    ) {
        roomTypeService.deleteRoomType(roomTypeId);
        return ApiResponse.<Void>builder()
                .message("Room Type has been deleted")
                .build();
    }

    @Operation(
            summary = "Upload Room Type Image",
            description = "Upload an image file for a room type and return the saved file path."
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
                    responseCode = "401",
                    description = SwaggerResponseMessages.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = SwaggerResponseMessages.FORBIDDEN
            )
    })
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/upload")
    public ApiResponse<String> uploadImage(
            @Parameter(
                    description = "Image file to upload."
            )
            @RequestParam("file")
            MultipartFile file
    ) throws IOException {
        String filePath = fileService.saveFile(file);
        return ApiResponse.<String>builder()
                .result(filePath)
                .build();
    }
}
