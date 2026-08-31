package com.example.roombooking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.roombooking.entity.BookingEntity;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    @Query("SELECT b FROM BookingEntity b JOIN FETCH b.room WHERE b.id = :id AND b.user.id = :userId")
    Optional<BookingEntity> findByIdAndUserId(Long id, Long userId);
    @Query("SELECT b FROM BookingEntity b " +
        "JOIN FETCH b.room "+
       "WHERE b.user.id = :userId " +
       "AND b.endAt > :startFrom " +
       "AND b.startAt < :endTo")
    Page<BookingEntity> findAllBookingsInPeriodByUserId(
        @Param("userId") Long userId,
        @Param("startFrom") LocalDateTime startFrom, 
        @Param("endTo") LocalDateTime endTo, 
        Pageable pageable);

    @Query("""
        SELECT COUNT(b) > 0
        FROM BookingEntity b
        WHERE b.room.id = :roomId
        AND b.endAt > :startFrom
        AND b.startAt < :endTo
        """)
    boolean existsByRoomIdAndPeriod(
        @Param("roomId") Long roomId,
        @Param("startFrom") LocalDateTime startFrom,
        @Param("endTo") LocalDateTime endTo);

    @Query("""
        SELECT COUNT(b) > 0
        FROM BookingEntity b
        WHERE b.room.id = :roomId
        AND b.id <> :excludeId
        AND b.endAt > :startFrom
        AND b.startAt < :endTo
        """)
    boolean existsByRoomIdAndPeriodExcluding(
        @Param("roomId") Long roomId,
        @Param("startFrom") LocalDateTime startFrom,
        @Param("endTo") LocalDateTime endTo,
        @Param("excludeId") Long excludeId);

    boolean existsByIdAndUserId(Long id, Long userId);
    void deleteByIdAndUserId(Long id, Long userId);

    @Query("""
        SELECT b FROM BookingEntity b
        JOIN FETCH b.room 
        WHERE b.room.id = :roomId
          AND b.endAt   > :from
          AND b.startAt < :to
        """)
    List<BookingEntity> findByRoomIdAndPeriod(
        @Param("roomId") Long roomId,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to);
}
