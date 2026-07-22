package com.example.hotel_booking.room.repository;

import com.example.hotel_booking.room.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @EntityGraph(attributePaths = {"roomType"})
    Page<Room> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"roomType"})
    Optional<Room> findById(Long id);

    boolean existsByRoomNumber(String roomNumber);

    long countByRoomTypeIdAndActiveTrue(Long roomTypeId);

    long countByStatus(com.example.hotel_booking.common.RoomStatus status);

    @Query("""
        SELECT r FROM Room r
        WHERE r.roomType.id = :roomTypeId
        AND r.active = true
        AND r.id NOT IN (
            SELECT br.room.id FROM BookingRoom br
            JOIN br.booking b
            WHERE b.status IN ('PENDING_DEPOSIT', 'CONFIRMED', 'CHECKED_IN')
            AND b.arrivalDate < :checkOut
            AND b.departureDate > :checkIn
        )
    """)
    List<Room> findAvailableRooms(
            @Param("roomTypeId") Long roomTypeId,
            @Param("checkIn") LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut
    );

    @Query("SELECT r FROM Room r JOIN FETCH r.roomType")
    List<Room> findAllWithRoomType();

    long countByActiveTrue();

    @EntityGraph(attributePaths = {"roomType"})
    @Query("""
        SELECT r FROM Room r
        WHERE (:roomNumber IS NULL OR r.roomNumber LIKE %:roomNumber%)
        AND (:status IS NULL OR r.status = :status)
        AND (:active IS NULL OR r.active = :active)
        AND (:floor IS NULL OR r.floor = :floor)
        AND (:roomTypeId IS NULL OR r.roomType.id = :roomTypeId)
    """)
    Page<Room> findWithFilter(
            @Param("roomNumber") String roomNumber,
            @Param("status") com.example.hotel_booking.common.RoomStatus status,
            @Param("active") Boolean active,
            @Param("floor") Integer floor,
            @Param("roomTypeId") Long roomTypeId,
            Pageable pageable
    );
    @Query("SELECT DISTINCT r.floor FROM Room r ORDER BY r.floor ASC")
    List<Integer> findDistinctFloors();


}