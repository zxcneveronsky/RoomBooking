package com.example.roombooking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.roombooking.entity.RoomEntity;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {

    @EntityGraph(attributePaths = {"options"})
    Optional<RoomEntity> findById(Long id);

    @EntityGraph(attributePaths = {"options"})
    @Query("""
        SELECT r FROM RoomEntity r
        WHERE (:name IS NULL OR LENGTH(TRIM(CAST(:name AS string))) = 0 OR LOWER(r.name) LIKE CONCAT('%', LOWER(CAST(:name AS string)), '%'))
          AND (:capacity IS NULL OR r.capacity >= :capacity)
          AND (:floor IS NULL OR r.floor = :floor)
          AND (:optionIds IS NULL OR NOT EXISTS (SELECT 1 FROM OptionEntity o WHERE o.id IN :optionIds) OR NOT EXISTS (
                SELECT 1 FROM OptionEntity o
                WHERE o.id IN :optionIds AND o NOT MEMBER OF r.options))
        """)
    Page<RoomEntity> searchRoom(
        @Param("name") String name,
        @Param("capacity") Integer capacity,
        @Param("floor") Integer floor,
        @Param("optionIds") List<Long> optionIds,
        Pageable pageable
    );

    @EntityGraph(attributePaths = {"options"})
    @Query("""
        SELECT r FROM RoomEntity r
        WHERE (:name IS NULL OR LENGTH(TRIM(CAST(:name AS string))) = 0 OR LOWER(r.name) LIKE CONCAT('%', LOWER(CAST(:name AS string)), '%'))
          AND r.capacity >= :requiredCapacity
          AND (:floor IS NULL OR r.floor = :floor)
          AND NOT EXISTS (
                SELECT 1 FROM BookingEntity b
                WHERE b.room = r
                  AND b.endAt   > :startFrom
                  AND b.startAt < :endTo)
          AND (:optionIds IS NULL OR NOT EXISTS (SELECT 1 FROM OptionEntity o WHERE o.id IN :optionIds) OR NOT EXISTS (
                SELECT 1 FROM OptionEntity o
                WHERE o.id IN :optionIds AND o NOT MEMBER OF r.options))
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
