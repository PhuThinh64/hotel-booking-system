package com.example.hotel_booking.payment.repository;

import com.example.hotel_booking.common.PaymentType;
import com.example.hotel_booking.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    Optional<Payment> findByTransactionId(String transactionId);
    boolean existsByTransactionId(String transactionId);
    List<Payment> findByBookingId(Long bookingId);

    List<Payment> findByBookingIdIn(List<Long> bookingIds);


    // Truy vấn tính tổng doanh thu thành công trong một khoảng thời gian
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentType IN :types AND p.status = 'SUCCESS' AND p.paidAt BETWEEN :start AND :end")
    BigDecimal sumRevenueByTypes(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end,
                                 @Param("types") List<PaymentType> types);


}
