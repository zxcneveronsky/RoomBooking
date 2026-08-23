package com.example.roombooking.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.roombooking.entity.BookingEntity;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    Optional<BookingEntity> findByIdAndUserId(Long id, Long userId);

    Page<BookingEntity> findAllByUserIdAndStartAtGreaterThanEqualAndEndAtLessThanEqual(
            Long userId, LocalDateTime startFrom, LocalDateTime endTo, Pageable pageable);

    boolean existsByIdAndUserId(Long id, Long userId);

    void deleteByIdAndUserId(Long id, Long userId);
}
