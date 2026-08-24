package com.example.roombooking.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.roombooking.entity.RoomEntity;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {

    @Query("SELECT r FROM RoomEntity r " +
       "WHERE r.capacity >= :requiredCapacity " +
       "AND NOT EXISTS (" +
       "  SELECT b FROM BookingEntity b " +
       "  WHERE b.room = r " +
       "  AND b.endAt > :startFrom " +
       "  AND b.startAt < :endTo" +
       ")")
    Page<RoomEntity> findAvailableByCapacityAndPeriod(
        @Param("requiredCapacity") int requiredCapacity, 
        @Param("startFrom") LocalDateTime startFrom, 
        @Param("endTo") LocalDateTime endTo,
        Pageable pageable
    );
    
}
