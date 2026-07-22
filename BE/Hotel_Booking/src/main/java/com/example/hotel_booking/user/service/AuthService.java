package com.example.hotel_booking.user.service;

import com.example.hotel_booking.user.dto.AuthRequest;
import com.example.hotel_booking.user.dto.AuthResponse;
import com.example.hotel_booking.user.dto.RegisterRequest;
import com.example.hotel_booking.user.dto.ForgotPasswordRequest;
import com.example.hotel_booking.user.dto.ResetPasswordRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse authenticate(AuthRequest request);
    void sendForgotPasswordToken(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}