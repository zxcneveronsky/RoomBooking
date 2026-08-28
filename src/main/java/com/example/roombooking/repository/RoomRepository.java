package com.example.roombooking.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.roombooking.entity.RoomEntity;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {

    @EntityGraph(attributePaths = {"options"})
    @Query("""
        SELECT r FROM RoomEntity r
        WHERE (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND r.capacity >= :requiredCapacity
          AND (:floor IS NULL OR r.floor = :floor)
          AND NOT EXISTS (
                SELECT 1 FROM BookingEntity b
                WHERE b.room = r
                  AND b.endAt   > :startFrom
                  AND b.startAt < :endTo)
          AND (:optionIds IS NULL OR (
                SELECT COUNT(o2.id) FROM r.options o2
                WHERE o2.id IN :optionIds
              ) = SIZE(:optionIds))
        """)
    Page<RoomEntity> searchAvailableRoom(
        @Param("name") String name,
        @Param("requiredCapacity") Integer requiredCapacity,
        @Param("floor") Integer floor,
        @Param("optionIds") List<Long> optionIds,
        @Param("startFrom") LocalDateTime startFrom,
        @Param("endTo") LocalDateTime endTo,
        Pageable pageable
    );
}
