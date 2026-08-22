package com.example.roombooking.exception;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(Long id) {
        super("Переговорная с id " + id + " не найдена.");
    }
}
