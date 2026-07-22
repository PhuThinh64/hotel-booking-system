package com.example.hotel_booking.user.service;

public interface EmailService {
    void sendPasswordResetEmail(String toEmail, String resetLink);
}
