package com.example.roombooking.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.roombooking.entity.BookingEntity;
import com.example.roombooking.exception.BookingNotFoundException;
import com.example.roombooking.repository.BookingRepository;
import com.example.roombooking.repository.RoomRepository;
import com.example.roombooking.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public BookingEntity getBookingById(Long bookingId, Long userId) {
        return bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
    }

    @Transactional
    public void deleteBooking(Long bookingId, Long userId) {
        if (!bookingRepository.existsByIdAndUserId(bookingId, userId)) {
            throw new BookingNotFoundException(bookingId);
        }
        bookingRepository.deleteByIdAndUserId(bookingId, userId);
    }

    @Transactional(readOnly = true)
    public Page<BookingEntity> getAllBookings(Long userId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        LocalDateTime startFrom = from != null ? from : LocalDateTime.MIN;
        LocalDateTime endTo = to != null ? to : LocalDateTime.MAX;
        return bookingRepository.findAllByUserIdAndStartAtGreaterThanEqualAndEndAtLessThanEqual(
                userId, startFrom, endTo, pageable);
    }
}
