package com.example.roombooking.dto.response;

import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        Long roomId,
        String roomName,
        LocalDateTime startAt,
        LocalDateTime endAt
){}
