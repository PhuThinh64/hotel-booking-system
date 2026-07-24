package com.example.hotel_booking.customer.controller;

import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.config.swagger.constants.ApiInfoConstants;
import com.example.hotel_booking.config.swagger.constants.ParameterDescriptions;
import com.example.hotel_booking.config.swagger.constants.SwaggerResponseMessages;
import com.example.hotel_booking.config.swagger.constants.SwaggerTags;
import com.example.hotel_booking.customer.dto.CreateCustomerRequest;
import com.example.hotel_booking.customer.dto.CustomerResponse;
import com.example.hotel_booking.customer.dto.UpdateCustomerRequest;
import com.example.hotel_booking.customer.service.CustomerService;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(
        name = SwaggerTags.CUSTOMER,
        description = "Manage customer profiles, lookup, filtering, and customer statistics operations."
)
@SecurityRequirement(name = ApiInfoConstants.SECURITY_SCHEME)
public class CustomerController {

    private final CustomerService customerService;

    @Operation(
            summary = "Get Customer by Phone Number",
            description = "Retrieve detailed customer information using their phone number."
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
    @GetMapping("/phone/{phoneNumber}")
    public ApiResponse<CustomerResponse> getByPhone(

            @Parameter(
                    description = ParameterDescriptions.PHONE,
                    example = "0912345678"
            )
            @PathVariable
            String phoneNumber
    ) {

        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.getByPhoneNumber(phoneNumber))
                .build();
    }

    @Operation(
            summary = "Create Customer Profile",
            description = "Create a new customer profile in the system after validating information."
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = SwaggerResponseMessages.CONFLICT
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CustomerResponse> create(

            @RequestBody
            @Valid
            CreateCustomerRequest request
    ) {

        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.createCustomer(request))
                .build();
    }

    @Operation(
            summary = "Get Customer Details by ID",
            description = "Retrieve detailed information of a specific customer by identifier."
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
    public ApiResponse<CustomerResponse> getById(

            @Parameter(
                    description = "Customer ID",
                    example = "1"
            )
            @PathVariable
            Long id
    ) {

        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.getById(id))
                .build();
    }

    @Operation(
            summary = "Retrieve Customer List",
            description = "Retrieve a paginated list of customers with optional search keyword and active status filtering."
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
    public ApiResponse<Page<CustomerResponse>> getAll(

            @Parameter(
                    description = "Search keyword for customer name, phone number, or email.",
                    example = "Nguyen"
            )
            @RequestParam(required = false)
            String keyword,

            @Parameter(
                    description = "Filter by active status.",
                    example = "true"
            )
            @RequestParam(required = false)
            Boolean active,

            @PageableDefault(size = 10)
            Pageable pageable
    ) {

        return ApiResponse.<Page<CustomerResponse>>builder()
                .result(customerService.getAllCustomers(keyword, active, pageable))
                .build();
    }

    @Operation(
            summary = "Update Customer Information",
            description = "Update existing customer profile details by identifier."
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
    @PutMapping("/{id}")
    public ApiResponse<CustomerResponse> update(

            @Parameter(
                    description = "Customer ID",
                    example = "1"
            )
            @PathVariable
            Long id,

            @RequestBody
            @Valid
            UpdateCustomerRequest request
    ) {

        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.updateCustomer(id, request))
                .build();
    }

    @Operation(
            summary = "Delete / Deactivate Customer",
            description = "Delete or deactivate a customer profile by identifier."
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
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleleCustomer(

            @Parameter(
                    description = "Customer ID",
                    example = "1"
            )
            @PathVariable
            Long id
    ) {

        customerService.deleteCustomer(id);
        return ApiResponse.<Void>builder()
                .message("Cập nhật trạng thái khách hàng thành công.")
                .build();
    }

    @Operation(
            summary = "Get New Customers Count Statistics",
            description = "Retrieve the total count of newly created customers within a specified date range."
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
    @GetMapping("/stats/count-new")
    public ApiResponse<Long> getNewCustomersCount(

            @Parameter(
                    description = "Start date for statistics range (ISO format).",
                    example = "2026-07-01T00:00:00"
            )
            @RequestParam("startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @Parameter(
                    description = "End date for statistics range (ISO format).",
                    example = "2026-07-31T23:59:59"
            )
            @RequestParam("endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate
    ) {

        long count = customerService.countNewCustomers(startDate, endDate);
        return ApiResponse.<Long>builder()
                .code(1000)
                .message("Lấy thống kê số lượng khách hàng mới thành công")
                .result(count)
                .build();
    }
}