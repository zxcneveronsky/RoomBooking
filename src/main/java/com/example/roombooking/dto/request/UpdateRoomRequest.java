package com.example.roombooking.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateRoomRequest(
        @NotNull(message = "ID переговорной не может быть пустым")
        Long id,
        String name,
        @Positive(message = "Вместимость должна быть положительной")
        Integer capacity,
        @PositiveOrZero(message = "Этаж не может быть отрицательным")
        Integer floor,
        String description,
        List<@NotNull(message = "ID опции не может быть пустым")Long> optionIds
) {}
