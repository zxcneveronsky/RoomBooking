package com.example.roombooking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.roombooking.entity.BookingEntity;
import com.example.roombooking.exception.BookingNotFoundException;
import com.example.roombooking.exception.RoomAlreadyBookedException;
import com.example.roombooking.exception.RoomNotFoundException;
import com.example.roombooking.repository.BookingRepository;
import com.example.roombooking.repository.RoomRepository;
import com.example.roombooking.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public List<BookingEntity> getRoomSchedule(Long roomId, LocalDate date) {
        if (!roomRepository.existsById(roomId)) {
            throw new RoomNotFoundException(roomId);
        }
        return bookingRepository.findByRoomIdAndPeriod(
                roomId,
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay());
    }

    @Transactional(readOnly = true)
    public Page<BookingEntity> getAllBookings(Long userId, LocalDate date, Pageable pageable) {
        return bookingRepository.findAllBookingsInPeriodByUserId(
                userId,
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay(),
                pageable);
    }

    @Transactional(readOnly = true)
    public BookingEntity getBookingById(Long bookingId, Long userId) {
        return bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
    }

    @Transactional
    public BookingEntity createBooking(BookingEntity bookingEntity, Long userId, Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new RoomNotFoundException(roomId);
        }
        if (bookingRepository.existsByRoomIdAndPeriod(roomId, bookingEntity.getStartAt(), bookingEntity.getEndAt())) {
            throw new RoomAlreadyBookedException();
        }
        bookingEntity.setUser(userRepository.getReferenceById(userId));
        bookingEntity.setRoom(roomRepository.getReferenceById(roomId));
        return bookingRepository.save(bookingEntity);
    }

    @Transactional
    public BookingEntity updateBooking(BookingEntity bookingUpdate, Long userId, Long roomId) {
        Long bookingId = bookingUpdate.getId();
        BookingEntity updatedBooking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .map(existingBooking -> {
                    if (!roomRepository.existsById(roomId)) {
                        throw new RoomNotFoundException(roomId);
                    }
                    existingBooking.setRoom(roomRepository.getReferenceById(roomId));
                    LocalDateTime start = bookingUpdate.getStartAt() != null ? bookingUpdate.getStartAt() : existingBooking.getStartAt();
                    LocalDateTime end = bookingUpdate.getEndAt() != null ? bookingUpdate.getEndAt() : existingBooking.getEndAt();
                    if (bookingRepository.existsByRoomIdAndPeriodExcluding(roomId, start, end, bookingId)) {
                        throw new RoomAlreadyBookedException();
                    }
                    existingBooking.setStartAt(start);
                    existingBooking.setEndAt(end);
                    return bookingRepository.save(existingBooking);
                })
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
        return updatedBooking;
    }

    @Transactional
    public void deleteBooking(Long bookingId, Long userId) {
        if (!bookingRepository.existsByIdAndUserId(bookingId, userId)) {
            throw new BookingNotFoundException(bookingId);
        }
        bookingRepository.deleteByIdAndUserId(bookingId, userId);
    }

}
