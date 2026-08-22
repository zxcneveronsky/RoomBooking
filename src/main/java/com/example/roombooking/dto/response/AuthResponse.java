package com.example.roombooking.dto.response;

public record AuthResponse(
    String token,
    String email,
    String role
) {}
