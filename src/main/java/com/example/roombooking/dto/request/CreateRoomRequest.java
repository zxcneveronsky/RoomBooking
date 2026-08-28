package com.example.roombooking.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateRoomRequest(
        @NotBlank(message = "Название комнаты не может быть пустым")
        String name,
        @NotNull(message = "Вместимость не может быть пустой")
        @Positive(message = "Вместимость должна быть положительной")
        Integer capacity,
        @NotNull(message = "Этаж не может быть пустым")
        @PositiveOrZero(message = "Этаж не может быть отрицательным")
        Integer floor,
        @NotBlank(message = "Описание не может быть пустым")
        String description,
        @NotEmpty(message = "Список опций не может быть пустым")
        List<@NotNull(message = "ID опции не может быть пустым")Long> optionIds
) {}
