package com.example.hotel_booking.user.service.impl;

import com.example.hotel_booking.user.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    @Qualifier("taskExecutor")
    private final TaskExecutor taskExecutor;

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        
        taskExecutor.execute(() -> {
            System.out.println("Đang gửi mail trên thread: " + Thread.currentThread().getName());

            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(toEmail);
                message.setSubject("[Khách Sạn] Yêu cầu đặt lại mật khẩu của bạn");

                String content = "Chào bạn,\n\n"
                        + "Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản liên kết với email này.\n"
                        + "Vui lòng nhấp vào liên kết bên dưới để thực hiện thay đổi mật khẩu (Liên kết có hiệu lực trong 10 phút):\n\n"
                        + resetLink + "\n\n"
                        + "Nếu bạn không thực hiện yêu cầu này, xin vui lòng bỏ qua email này.\n\n"
                        + "Trân trọng,\nBan quản trị khách sạn.";

                message.setText(content);
                mailSender.send(message);
                System.out.println("Gửi mail thành công!");
            } catch (Exception e) {
                System.err.println("Lỗi khi gửi mail: " + e.getMessage());
            }
        });
    }
}