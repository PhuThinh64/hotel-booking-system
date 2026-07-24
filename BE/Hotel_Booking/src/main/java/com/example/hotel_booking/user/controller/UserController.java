package com.example.hotel_booking.user.controller;

import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.common.exception.ErrorCode;
import com.example.hotel_booking.config.swagger.constants.ApiInfoConstants;
import com.example.hotel_booking.config.swagger.constants.SwaggerResponseMessages;
import com.example.hotel_booking.config.swagger.constants.SwaggerTags;
import com.example.hotel_booking.user.dto.BaseProfileResponse;
import com.example.hotel_booking.user.dto.ChangePasswordRequest;
import com.example.hotel_booking.user.dto.CustomerProfileUpdateRequest;
import com.example.hotel_booking.user.dto.EmployeeProfileUpdateRequest;
import com.example.hotel_booking.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(
        name = SwaggerTags.USER,
        description = "Manage authenticated user profiles, customer/employee profile updates, and password changes."
)
@SecurityRequirement(name = ApiInfoConstants.SECURITY_SCHEME)
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Get Current User Profile",
            description = "Retrieve the profile information of the currently authenticated user."
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
    @GetMapping("/profile")
    public ApiResponse<BaseProfileResponse> getProfile() {

        return ApiResponse.<BaseProfileResponse>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message(ErrorCode.SUCCESS.getMessage())
                .result(userService.getProfile())
                .build();
    }

    @Operation(
            summary = "Update Customer Profile",
            description = "Update profile details for the currently authenticated customer user."
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
    @PutMapping("/profile/customer")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ApiResponse<BaseProfileResponse> updateCustomerProfile(
            @RequestBody @Valid CustomerProfileUpdateRequest request
    ) {

        return ApiResponse.<BaseProfileResponse>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Cập nhật hồ sơ khách hàng thành công!")
                .result(userService.updateCustomerProfile(request))
                .build();
    }

    @Operation(
            summary = "Update Employee Profile",
            description = "Update profile details for the currently authenticated employee user (Admin / Receptionist)."
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
    @PutMapping("/profile/employee")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ApiResponse<BaseProfileResponse> updateEmployeeProfile(
            @RequestBody @Valid EmployeeProfileUpdateRequest request
    ) {

        return ApiResponse.<BaseProfileResponse>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Cập nhật hồ sơ nhân viên thành công!")
                .result(userService.updateEmployeeProfile(request))
                .build();
    }

    @Operation(
            summary = "Change Password",
            description = "Change current user password after verifying old password."
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
            )
    })
    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(
            @RequestBody @Valid ChangePasswordRequest request
    ) {

        userService.changePassword(request);
        return ApiResponse.<String>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Đổi mật khẩu thành công!")
                .build();
    }
}