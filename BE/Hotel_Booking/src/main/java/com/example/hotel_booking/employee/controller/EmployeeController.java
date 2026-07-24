package com.example.hotel_booking.employee.controller;

import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.common.exception.ErrorCode;
import com.example.hotel_booking.config.swagger.constants.ApiInfoConstants;
import com.example.hotel_booking.config.swagger.constants.SwaggerResponseMessages;
import com.example.hotel_booking.config.swagger.constants.SwaggerTags;
import com.example.hotel_booking.employee.dto.EmployeeAdminUpdateRequest;
import com.example.hotel_booking.employee.dto.EmployeeCreateRequest;
import com.example.hotel_booking.employee.dto.EmployeeResponse;
import com.example.hotel_booking.employee.service.EmployeeService;
import com.example.hotel_booking.user.service.UserService;
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
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(
        name = SwaggerTags.EMPLOYEE,
        description = "Manage employee profiles, account creation, updates, deactivation, and password reset operations."
)
@SecurityRequirement(name = ApiInfoConstants.SECURITY_SCHEME)
public class EmployeeController {

    private final EmployeeService employeeService;
    private final UserService userService;

    @Operation(
            summary = "Retrieve Employee List",
            description = "Retrieve a paginated list of employees with optional search keyword and active status filtering."
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
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ApiResponse<Page<EmployeeResponse>> getAll(

            @Parameter(
                    description = "Search keyword for employee name, phone number, or email.",
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

        return ApiResponse.<Page<EmployeeResponse>>builder()
                .result(employeeService.getAllEmployees(keyword, active, pageable))
                .build();
    }

    @Operation(
            summary = "Create Employee Profile",
            description = "Create a new employee profile along with user account credentials."
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
    public ApiResponse<EmployeeResponse> create(

            @RequestBody
            @Valid
            EmployeeCreateRequest request
    ) {

        return ApiResponse.<EmployeeResponse>builder()
                .result(employeeService.createEmployee(request))
                .build();
    }

    @Operation(
            summary = "Update Employee Profile",
            description = "Update employee information and assigned role by ID."
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
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<EmployeeResponse> update(

            @Parameter(
                    description = "Employee ID",
                    example = "1"
            )
            @PathVariable
            Long id,

            @RequestBody
            @Valid
            EmployeeAdminUpdateRequest request
    ) {

        return ApiResponse.<EmployeeResponse>builder()
                .result(employeeService.updateEmployee(id, request))
                .build();
    }

    @Operation(
            summary = "Delete / Deactivate Employee",
            description = "Deactivate an employee account by ID."
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
    public ApiResponse<Void> deleteEmployee(

            @Parameter(
                    description = "Employee ID",
                    example = "1"
            )
            @PathVariable
            Long id
    ) {

        employeeService.deleteEmployee(id);
        return ApiResponse.<Void>builder()
                .message("Cập nhật trạng thái nhân viên thành công")
                .build();
    }

    @Operation(
            summary = "Reset Employee Password",
            description = "Reset password for an employee user account to default setting."
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
    @PostMapping("/{userId}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> resetPassword(

            @Parameter(
                    description = "User ID associated with the employee",
                    example = "10"
            )
            @PathVariable
            Long userId
    ) {

        userService.resetPasswordForEmployee(userId);
        return ApiResponse.<String>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Reset mật khẩu thành công!")
                .build();
    }
}