package com.example.roombooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRoomRequest(
        @NotNull
        @NotBlank(message = "Название комнаты не может быть пустым")
        String name,
        @NotBlank(message = "описание не может быть пустым")
        String description
) {}
