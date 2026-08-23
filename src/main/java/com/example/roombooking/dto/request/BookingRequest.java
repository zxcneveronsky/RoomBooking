package com.example.roombooking.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookingRequest(
        @NotNull(message="ID комнаты не может быть пустым")
        Long roomId,

        @NotNull(message = "Дата начала обязательна")
        @FutureOrPresent(message="Дата начала не может быть в прошлом")
        LocalDateTime startAt,

        @NotNull(message = "Дата окончания обязательна")
        @FutureOrPresent(message="Дата окончания не может быть в прошлом")
        LocalDateTime endAt
){}
