package com.example.roombooking.dto.response;

public record RoomResponse(
        Long id,
        String name,
        Integer capacity,
        String description
) {
}
