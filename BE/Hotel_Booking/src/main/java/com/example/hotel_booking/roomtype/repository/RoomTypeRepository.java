package com.example.hotel_booking.roomtype.repository;

import com.example.hotel_booking.roomtype.entity.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    boolean existsByName(String name);

    
    @Query("SELECT rt FROM RoomType rt WHERE " +
            "(:active IS NULL OR rt.active = :active) AND " +
            "(:name IS NULL OR LOWER(rt.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<RoomType> findAllWithFilters(@Param("active") Boolean active,
                                      @Param("name") String name,
                                      Pageable pageable);
}