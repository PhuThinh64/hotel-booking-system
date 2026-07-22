package com.example.hotel_booking.customer.repository;

import com.example.hotel_booking.customer.entity.Customer;
import com.example.hotel_booking.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByIdentityCard(String identityCard);

    Optional<Customer> findByUser(User user);

    Optional<Customer> findByUserId(Long userId);

    Page<Customer> findByActive(boolean active, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE (:active IS NULL OR c.active = :active) AND " +
            "(LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "c.phoneNumber LIKE CONCAT('%', :keyword, '%'))")
    Page<Customer> searchCustomers(@Param("active") Boolean active,
                                   @Param("keyword") String keyword,
                                   Pageable pageable);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    long countNewCustomersByTimeRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}