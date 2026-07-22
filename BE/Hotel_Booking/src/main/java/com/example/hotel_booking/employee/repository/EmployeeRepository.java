package com.example.hotel_booking.employee.repository;

import com.example.hotel_booking.employee.entity.Employee;
import com.example.hotel_booking.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUser(User user);

    
    Page<Employee> findByActive(boolean active, Pageable pageable);

    
    @Query("SELECT e FROM Employee e WHERE (:active IS NULL OR e.active = :active) AND " +
            "(LOWER(e.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "e.phoneNumber LIKE CONCAT('%', :keyword, '%') OR " +
            "LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Employee> searchEmployees(@Param("active") Boolean active,
                                   @Param("keyword") String keyword,
                                   Pageable pageable);
}