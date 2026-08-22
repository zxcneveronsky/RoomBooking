package com.example.roombooking.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateRoomRequest(
        @NotNull(message = "ID переговорной не может быть пустым")
        Long id,
        String name,
        String description
) {}
