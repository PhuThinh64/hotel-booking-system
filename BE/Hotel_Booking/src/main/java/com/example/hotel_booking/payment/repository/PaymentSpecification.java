package com.example.hotel_booking.payment.repository;

import com.example.hotel_booking.booking.entity.Booking;
import com.example.hotel_booking.common.PaymentMethod;
import com.example.hotel_booking.common.PaymentStatus;
import com.example.hotel_booking.common.PaymentType;
import com.example.hotel_booking.payment.entity.Payment;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PaymentSpecification {

    
    public static Specification<Payment> filterRefunds(String keyword, String status, String method, String startDate, String endDate) {
        return (root, query, cb) -> {
            
            Predicate predicate = cb.equal(root.get("paymentType"), PaymentType.REFUND);

            
            if (StringUtils.hasText(status)) {
                if ("PENDING".equalsIgnoreCase(status)) {
                    
                    predicate = cb.and(predicate, root.get("status").in(PaymentStatus.PENDING));
                } else if ("HISTORY".equalsIgnoreCase(status)) {
                    
                    predicate = cb.and(predicate, root.get("status").in(PaymentStatus.SUCCESS, PaymentStatus.FAILED));
                }
            }

            
            if (StringUtils.hasText(method)) {
                try {
                    PaymentMethod paymentMethod = PaymentMethod.valueOf(method.toUpperCase());
                    predicate = cb.and(predicate, cb.equal(root.get("method"), paymentMethod));
                } catch (IllegalArgumentException e) {
                    
                }
            }

            
            if (StringUtils.hasText(startDate) && StringUtils.hasText(endDate)) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDateTime startOfDay = LocalDate.parse(startDate, formatter).atStartOfDay();
                    
                    LocalDateTime endOfDay = LocalDate.parse(endDate, formatter).atTime(23, 59, 59);

                    predicate = cb.and(predicate, cb.between(root.get("createdAt"), startOfDay, endOfDay));
                } catch (Exception e) {
                    
                }
            }

            
            if (StringUtils.hasText(keyword)) {
                String pattern = "%" + keyword.toLowerCase() + "%";

                
                Join<Payment, Booking> bookingJoin = root.join("booking", JoinType.LEFT);

                Predicate search = cb.or(
                        
                        cb.like(bookingJoin.get("id").as(String.class), pattern),

                        
                        
                        cb.like(cb.lower(bookingJoin.get("contactName")), pattern),

                        
                        cb.like(cb.lower(bookingJoin.get("contactPhone")), pattern)
                );

                predicate = cb.and(predicate, search);
            }

            return predicate;
        };
    }
}