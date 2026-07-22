package com.example.hotel_booking.booking.task;

import com.example.hotel_booking.booking.entity.Booking;
import com.example.hotel_booking.booking.repository.BookingRepository;
import com.example.hotel_booking.common.BookingRoomStatus;
import com.example.hotel_booking.common.BookingServiceStatus;
import com.example.hotel_booking.common.BookingStatus;
import com.example.hotel_booking.common.PaymentStatus;
import com.example.hotel_booking.payment.entity.Payment;
import com.example.hotel_booking.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingTask {
    private final BookingRepository bookingRepo;
    private final PaymentRepository paymentRepo;

    
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cancelExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();

        
        List<Booking> expiredBookings = bookingRepo
                .findByStatusAndExpiryDateBefore(BookingStatus.PENDING_DEPOSIT, now);

        if (!expiredBookings.isEmpty()) {
            List<Payment> paymentsToUpdate = new java.util.ArrayList<>();

            expiredBookings.forEach(booking -> {
                
                booking.setStatus(BookingStatus.CANCELLED);
                booking.setCancelReason("Tự động hủy do quá hạn thanh toán");

                
                booking.setRoomAmount(BigDecimal.ZERO);
                booking.setServiceAmount(BigDecimal.ZERO);
                booking.setTotalAmount(BigDecimal.ZERO);
                booking.setDepositAmount(BigDecimal.ZERO);

                List<Payment> payments = paymentRepo.findByBookingId(booking.getId());
                payments.forEach(payment -> {
                    if (payment.getStatus() == PaymentStatus.PENDING) {
                        payment.setStatus(PaymentStatus.FAILED);
                        paymentsToUpdate.add(payment);
                    }
                });

                
                if (booking.getBookingRooms() != null) {
                    booking.getBookingRooms().forEach(room -> {
                        room.setStatus(BookingRoomStatus.CANCELLED);
                        room.setCancelReason("Tự động hủy do quá hạn thanh toán");
                    });
                }

                if(booking.getBookingRooms() != null) {
                    booking.getBookingServices().forEach(service -> {
                        service.setStatus(BookingServiceStatus.CANCELLED);
                    });
                }

                log.info("Hệ thống tự động hủy đơn ID: {}", booking.getId());
            });
            bookingRepo.saveAll(expiredBookings);
            if (!paymentsToUpdate.isEmpty()) {
                paymentRepo.saveAll(paymentsToUpdate);
            }
        }
    }
}