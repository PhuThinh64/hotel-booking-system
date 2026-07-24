package com.example.hotel_booking.service.controller;

import com.example.hotel_booking.common.ServiceType;
import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.config.swagger.constants.ApiInfoConstants;
import com.example.hotel_booking.config.swagger.constants.SwaggerResponseMessages;
import com.example.hotel_booking.config.swagger.constants.SwaggerTags;
import com.example.hotel_booking.service.dto.CreateExtraServiceRequest;
import com.example.hotel_booking.service.dto.ExtraServiceResponse;
import com.example.hotel_booking.service.dto.UpdateExtraServiceRequest;
import com.example.hotel_booking.service.service.ExtraServiceService;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
@Tag(
        name = SwaggerTags.EXTRA_SERVICE,
        description = "Manage hotel extra services catalog, pricing, service types, and activation status."
)
@SecurityRequirement(name = ApiInfoConstants.SECURITY_SCHEME)
public class ExtraServiceController {

    private final ExtraServiceService extraServiceService;

    @Operation(
            summary = "Create Extra Service",
            description = "Create a new extra service entry in the catalog (Admin only)."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
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
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<ExtraServiceResponse> create(
            @RequestBody @Valid CreateExtraServiceRequest request
    ) {
        return ApiResponse.<ExtraServiceResponse>builder()
                .result(extraServiceService.createService(request))
                .build();
    }

    @Operation(
            summary = "Get All Extra Services",
            description = "Retrieve a paginated list of all extra services with optional filters for name, active status, and service type."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
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
    @GetMapping
    public ApiResponse<Page<ExtraServiceResponse>> getAll(
            @Parameter(description = "Filter by service name", example = "Laundry")
            @RequestParam(required = false) String name,

            @Parameter(description = "Filter by active status", example = "true")
            @RequestParam(required = false) Boolean active,

            @Parameter(description = "Filter by service type", example = "REGULAR")
            @RequestParam(required = false) ServiceType serviceType,

            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ApiResponse.<Page<ExtraServiceResponse>>builder()
                .result(extraServiceService.getAllServices(name, active, serviceType, pageable))
                .build();
    }

    @Operation(
            summary = "Get Public Extra Services",
            description = "Retrieve active extra services available for public guest viewing.",
            security = {}
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            )
    })
    @GetMapping("/public")
    public ApiResponse<Page<ExtraServiceResponse>> getPublicServices(
            @Parameter(description = "Filter by service name", example = "Spa")
            @RequestParam(required = false) String name,

            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ApiResponse.<Page<ExtraServiceResponse>>builder()
                .result(extraServiceService.getPublicServices(name, pageable))
                .build();
    }

    @Operation(
            summary = "Get Extra Service by ID",
            description = "Retrieve detailed information of a specific extra service by its ID."
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
    public ApiResponse<ExtraServiceResponse> getById(
            @Parameter(description = "Extra Service ID", example = "1")
            @PathVariable Long id
    ) {
        return ApiResponse.<ExtraServiceResponse>builder()
                .result(extraServiceService.getById(id))
                .build();
    }

    @Operation(
            summary = "Update Extra Service",
            description = "Update information, pricing, or status of an existing extra service by ID (Admin only)."
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<ExtraServiceResponse> update(
            @Parameter(description = "Extra Service ID", example = "1")
            @PathVariable Long id,

            @RequestBody @Valid UpdateExtraServiceRequest request
    ) {
        return ApiResponse.<ExtraServiceResponse>builder()
                .result(extraServiceService.updateService(id, request))
                .build();
    }

    @Operation(
            summary = "Deactivate Extra Service",
            description = "Deactivate an extra service by ID (Admin only)."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
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
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Void> delete(
            @Parameter(description = "Extra Service ID", example = "1")
            @PathVariable Long id
    ) {
        extraServiceService.deleteService(id);
        return ApiResponse.<Void>builder()
                .message("Service deactivated successfully")
                .build();
    }
}