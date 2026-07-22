package com.example.hotel_booking.service.repository;

import com.example.hotel_booking.common.ServiceType;
import com.example.hotel_booking.service.entity.ExtraService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExtraServiceRepository extends JpaRepository<ExtraService, Long> {

    Optional<ExtraService> findByNameIgnoreCase(String name);

    
    @Query("SELECT s FROM ExtraService s WHERE " +
            "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:active IS NULL OR s.active = :active) AND " +
            "(:serviceType IS NULL OR s.serviceType = :serviceType)")
    Page<ExtraService> findServicesForAdmin(
            @Param("name") String name,
            @Param("active") Boolean active,
            @Param("serviceType") ServiceType serviceType,
            Pageable pageable
    );

    
    @Query("SELECT s FROM ExtraService s WHERE " +
            "s.active = true AND s.serviceType = com.example.hotel_booking.common.ServiceType.REGULAR AND " +
            "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<ExtraService> findPublicServices(@Param("name") String name, Pageable pageable);
}