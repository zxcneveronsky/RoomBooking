package com.example.roombooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateRoomRequest(
        @NotBlank(message = "Название комнаты не может быть пустым")
        String name,
        @NotNull(message = "Вместимость не может быть пустой")
        @Positive(message = "Вместимость должна быть положительной")
        Integer capacity,
        @NotBlank(message = "Описание не может быть пустым")
        String description
) {}
