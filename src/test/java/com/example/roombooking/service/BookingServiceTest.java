package com.example.roombooking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.roombooking.entity.BookingEntity;
import com.example.roombooking.entity.RoomEntity;
import com.example.roombooking.entity.UserEntity;
import com.example.roombooking.exception.BookingNotFoundException;
import com.example.roombooking.exception.RoomAlreadyBookedException;
import com.example.roombooking.exception.RoomNotFoundException;
import com.example.roombooking.repository.BookingRepository;
import com.example.roombooking.repository.RoomRepository;
import com.example.roombooking.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void should_return_room_schedule() {
        Long roomId = 1L;
        LocalDate date = LocalDate.of(2026, 9, 5);

        BookingEntity booking = new BookingEntity();
        booking.setId(1L);

        when(roomRepository.existsById(roomId)).thenReturn(true);
        when(bookingRepository.findByRoomIdAndPeriod(
                eq(roomId),
                eq(date.atStartOfDay()),
                eq(date.plusDays(1).atStartOfDay())))
                .thenReturn(List.of(booking));

        List<BookingEntity> result = bookingService.getRoomSchedule(roomId, date);

        assertThat(result).hasSize(1);
    }

    @Test
    void should_throw_when_room_not_found_for_schedule() {
        when(roomRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> bookingService.getRoomSchedule(99L, LocalDate.now()))
            .isInstanceOf(RoomNotFoundException.class);
    }

    @Test
    void should_return_page_of_bookings() {
        Page<BookingEntity> page = new PageImpl<>(List.of(new BookingEntity()));

        when(bookingRepository.findAllBookingsInPeriodByUserId(
            eq(1L),
            any(LocalDateTime.class),
            any(LocalDateTime.class),
            any(Pageable.class)))
            .thenReturn(page);

        Page<BookingEntity> result = bookingService.getAllBookings(1L, LocalDate.now(), Pageable.ofSize(10));

        assertThat(result).hasSize(1);
    }

    @Test
    void should_return_booking_when_found() {
        BookingEntity booking = new BookingEntity();
        booking.setId(1L);

        when(bookingRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(booking));

        BookingEntity result = bookingService.getBookingById(1L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void should_throw_when_booking_not_found() {
        when(bookingRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingById(99L, 1L))
            .isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    void should_create_booking_when_room_available() {
        BookingEntity booking = new BookingEntity();
        booking.setStartAt(LocalDateTime.of(2026, 9, 5, 10, 0));
        booking.setEndAt(LocalDateTime.of(2026, 9, 5, 12, 0));

        RoomEntity room = new RoomEntity();
        room.setId(1L);

        UserEntity user = new UserEntity();
        user.setId(1L);

        when(bookingRepository.existsByRoomIdAndPeriod(eq(1L), any(), any())).thenReturn(false);
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingEntity result = bookingService.createBooking(booking, 1L, 1L);

        assertThat(result).isNotNull();
        verify(bookingRepository).save(booking);
    }

    @Test
    void should_throw_when_room_already_booked() {
        BookingEntity booking = new BookingEntity();
        booking.setStartAt(LocalDateTime.of(2026, 9, 5, 10, 0));
        booking.setEndAt(LocalDateTime.of(2026, 9, 5, 12, 0));

        when(bookingRepository.existsByRoomIdAndPeriod(eq(1L), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> bookingService.createBooking(booking, 1L, 1L))
            .isInstanceOf(RoomAlreadyBookedException.class);
    }


    @Test
    void should_update_booking() {
        BookingEntity existing = new BookingEntity();
        existing.setId(1L);
        existing.setStartAt(LocalDateTime.of(2026, 9, 5, 10, 0));
        existing.setEndAt(LocalDateTime.of(2026, 9, 5, 12, 0));

        BookingEntity update = new BookingEntity();
        update.setId(1L);
        update.setStartAt(LocalDateTime.of(2026, 9, 5, 14, 0));
        update.setEndAt(LocalDateTime.of(2026, 9, 5, 16, 0));

        when(bookingRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(existing));
        when(roomRepository.existsById(1L)).thenReturn(true);
        when(roomRepository.getReferenceById(1L)).thenReturn(new RoomEntity());
        when(bookingRepository.existsByRoomIdAndPeriodExcluding(eq(1L), any(), any(), eq(1L))).thenReturn(false);
        when(bookingRepository.save(any())).thenReturn(existing);

        BookingEntity result = bookingService.updateBooking(update, 1L, 1L);

        assertThat(result).isNotNull();
        verify(bookingRepository).save(existing);
    }

    @Test
    void should_delete_booking_when_exists() {
        when(bookingRepository.existsByIdAndUserId(1L, 1L)).thenReturn(true);

        bookingService.deleteBooking(1L, 1L);

        verify(bookingRepository).deleteByIdAndUserId(1L, 1L);
    }

    @Test
    void should_throw_when_booking_not_found_for_delete() {
        when(bookingRepository.existsByIdAndUserId(99L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> bookingService.deleteBooking(99L, 1L))
            .isInstanceOf(BookingNotFoundException.class);

        verify(bookingRepository, never()).deleteByIdAndUserId(any(), any());
    }

}