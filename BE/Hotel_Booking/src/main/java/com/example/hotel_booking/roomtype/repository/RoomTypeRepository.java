package com.example.hotel_booking.roomtype.repository;

import com.example.hotel_booking.roomtype.entity.RoomType;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    boolean existsByName(String name);

    //Truy vấn động theo active và tìm kiếm gần đúng theo name
    @Query("SELECT rt FROM RoomType rt WHERE " +
            "(:active IS NULL OR rt.active = :active) AND " +
            "(:name IS NULL OR LOWER(rt.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<RoomType> findAllWithFilters(@Param("active") Boolean active,
                                      @Param("name") String name,
                                      Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT rt
    FROM RoomType rt
    WHERE rt.id = :id
""")
    Optional<RoomType> findByIdForUpdate(@Param("id") Long id);
}