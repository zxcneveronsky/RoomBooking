package com.example.roombooking.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateBookingRequest(
        @NotNull(message="ID брони не может быть пустым")
        Long id,
        @NotNull(message="ID комнаты не может быть пустым")
        Long roomId,
        @FutureOrPresent(message="Дата начала не может быть в прошлом")
        LocalDateTime startAt,
        @FutureOrPresent(message="Дата начала не может быть в прошлом")
        LocalDateTime endAt
){}
