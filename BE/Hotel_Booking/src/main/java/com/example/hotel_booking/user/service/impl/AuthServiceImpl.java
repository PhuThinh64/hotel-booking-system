package com.example.hotel_booking.user.service.impl;

import com.example.hotel_booking.common.exception.AppException;
import com.example.hotel_booking.common.exception.ErrorCode;
import com.example.hotel_booking.customer.entity.Customer;
import com.example.hotel_booking.customer.mapper.CustomerMapper;
import com.example.hotel_booking.customer.repository.CustomerRepository;
import com.example.hotel_booking.employee.entity.Employee;
import com.example.hotel_booking.employee.repository.EmployeeRepository;
import com.example.hotel_booking.role.repository.RoleRepository;
import com.example.hotel_booking.security.JwtService;
import com.example.hotel_booking.user.dto.*;
import com.example.hotel_booking.user.entity.User;
import com.example.hotel_booking.user.repository.UserRepository;
import com.example.hotel_booking.user.service.AuthService;
import com.example.hotel_booking.user.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService { // 1. Implement interface của bạn

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final CustomerMapper customerMapper;

    // 2. Inject EmailService bạn vừa viết vào đây
    private final EmailService emailService;

    // Đọc cấu hình URL Frontend từ file application.properties (nếu không có mặc định là localhost:5173)
    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại!");
        }

        var userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Role USER"));

        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .active(true)
                .build();
        var savedUser = userRepository.save(user);

        Customer customer = customerMapper.toEntityFromRegister(request);
        customer.setUser(savedUser);
        Customer savedCustomer = customerRepository.save(customer);

        return AuthResponse.builder()
                .token(null)
                .username(savedUser.getUsername())
                .role(userRole.getName())
                .userId(savedUser.getId())
                .profileId(savedCustomer.getId())
                .fullName(savedCustomer.getFullName())
                .phoneNumber(savedCustomer.getPhoneNumber())
                .build();
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var jwtToken = jwtService.generateToken(user);

        Long profileId = null;
        String fullName = null;
        String phoneNumber = null;
        String email = null;
        String roleName = user.getRole().getName();

        // Kiểm tra linh hoạt cả "USER" và "ROLE_USER" cho an toàn tuyệt đối với DB cũ
        if ("ROLE_USER".equals(roleName) || "USER".equals(roleName)) {
            Customer customer = customerRepository.findByUser(user)
                    .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
            profileId = customer.getId();
            fullName = customer.getFullName();
            phoneNumber = customer.getPhoneNumber();
            email = customer.getEmail();
        }
        else if ("ROLE_RECEPTIONIST".equals(roleName) || "ROLE_ADMIN".equals(roleName)) {
            Employee employee = employeeRepository.findByUser(user)
                    .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND));

            profileId = employee.getId();
            fullName = employee.getFullName();
            phoneNumber = employee.getPhoneNumber();
            email = employee.getEmail();
        }
        else {
            throw new AppException(ErrorCode.ROLE_NOT_SUPPORTED);
        }

        return AuthResponse.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .role(roleName)
                .userId(user.getId())
                .profileId(profileId)
                .fullName(fullName)
                .phoneNumber(phoneNumber)
                .email(email)
                .build();
    }

    // 3. Triển khai logic gửi link quên mật khẩu
    @Override
    @Transactional
    public void sendForgotPasswordToken(ForgotPasswordRequest request) {
        System.out.println("EmailService đang chạy trên: " + Thread.currentThread().getName());
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String email = null;
        String roleName = user.getRole().getName();

        // Bóc tách email dựa theo Role để biết gửi vào đâu
        if ("ROLE_USER".equals(roleName) || "USER".equals(roleName)) {
            Customer customer = customerRepository.findByUser(user)
                    .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
            email = customer.getEmail();
        }
        else if ("ROLE_RECEPTIONIST".equals(roleName) || "ROLE_ADMIN".equals(roleName)) {
            Employee employee = employeeRepository.findByUser(user)
                    .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND));
            email = employee.getEmail();
        }
        else {
            throw new AppException(ErrorCode.ROLE_NOT_SUPPORTED);
        }

        if (email == null || email.isBlank()) {
            throw new RuntimeException("Tài khoản này chưa được đăng ký địa chỉ Email hệ thống!");
        }

        // Tạo chuỗi token mã hóa ngắn hạn (bạn cần viết hàm generateResetToken(user) bên trong JwtService nhé)
        String resetToken = jwtService.generateResetToken(user);

        // Tạo đường dẫn gửi sang mail click về trang FE
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

        // Tiến hành gửi Mail
        System.out.println("AuthServiceImpl đang chạy trên: " + Thread.currentThread().getName());
        emailService.sendPasswordResetEmail(email, resetLink);
    }

    // 4. Triển khai logic đổi mật khẩu mới bằng Token nhận được
    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String token = request.getToken();
        String username;

        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            throw new RuntimeException("Mã xác thực liên kết không hợp lệ hoặc đã hết hạn!");
        }

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Kiểm tra xem token này có bị cũ/vô hiệu hóa không
        if (!jwtService.isTokenValid(token, user)) {
            throw new RuntimeException("Liên kết đã hết hạn hoặc không còn hiệu lực!");
        }

        // 1. Mã hóa mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // 2. CẬP NHẬT THỜI ĐIỂM ĐỔI MẬT KHẨU
        user.setPasswordChangedAt(LocalDateTime.now());

        // 3. Lưu lại
        userRepository.save(user);
    }
}