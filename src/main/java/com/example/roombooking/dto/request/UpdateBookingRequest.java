package com.example.roombooking.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateBookingRequest(
        @NotNull(message="ID брони не может быть пустым")
        Long id,
        Long roomId,
        LocalDateTime startAt,
        LocalDateTime endAt
){}
