package com.example.roombooking.dto.response;

import java.util.List;

public record RoomResponse(
        Long id,
        String name,
        Integer capacity,
        Integer floor,
        String description,
        List<OptionResponse> options
) {
}
