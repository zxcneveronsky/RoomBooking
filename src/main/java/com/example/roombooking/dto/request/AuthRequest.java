package com.example.roombooking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AuthRequest(
    @Email(message = "Некорректный email")
    @NotNull(message = "Email не может быть пустым")
    @NotBlank(message = "Email не может быть пустым")
    String email,
    @NotNull(message = "Пароль не может быть пустым")
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, max = 255, message = "Пароль не может быть слишком коротким и слишком длинным")
    String password
) {} 
