package com.example.roombooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateOptionRequest(
        @NotNull(message = "ID опции не может быть пустым")
        Long id,
        @NotBlank(message = "Название опции не может быть пустым")
        @Size(max = 255, message = "Название опции не может быть длиннее 255 символов")
        String name
) {}
