package com.example.roombooking.controller;

import com.example.roombooking.dto.request.CreateBookingRequest;
import com.example.roombooking.dto.request.UpdateBookingRequest;
import com.example.roombooking.dto.response.BookingResponse;
import com.example.roombooking.mapper.BookingMapper;
import com.example.roombooking.security.UserDetailsAdapter;
import com.example.roombooking.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @GetMapping("/{id}")
    public BookingResponse getBookingById(
        @AuthenticationPrincipal UserDetailsAdapter adapter,
        @PathVariable("id") Long bookingId) {
        return bookingMapper.toResponse(bookingService.getBookingById(bookingId, adapter.getUserId()));
    }

    @GetMapping
    public Page<BookingResponse> getAllBookings(
        @AuthenticationPrincipal UserDetailsAdapter adapter,
        @RequestParam("date") LocalDate date,
        Pageable pageable) {
        return bookingService.getAllBookings(adapter.getUserId(), date, pageable)
                .map(bookingMapper::toResponse);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(
        @AuthenticationPrincipal UserDetailsAdapter adapter,
        @Valid @RequestBody CreateBookingRequest request) {
        return bookingMapper.toResponse(bookingService.createBooking(bookingMapper.toEntity(request), adapter.getUserId(), request.roomId()));
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse updateBooking(
        @AuthenticationPrincipal UserDetailsAdapter adapter,
        @Valid @RequestBody UpdateBookingRequest request) {
        return bookingMapper.toResponse(bookingService.updateBooking(bookingMapper.toEntity(request), adapter.getUserId(), request.roomId()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(
        @AuthenticationPrincipal UserDetailsAdapter adapter,
        @PathVariable("id") Long bookingId) {
        bookingService.deleteBooking(bookingId, adapter.getUserId());
    }
}
