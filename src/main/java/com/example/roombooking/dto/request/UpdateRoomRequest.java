package com.example.roombooking.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateRoomRequest(
        @NotNull(message = "ID переговорной не может быть пустым")
        Long id,
        String name,
        @Positive(message = "Вместимость должна быть положительной")
        Integer capacity,
        String description
) {}
