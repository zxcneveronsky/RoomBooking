package com.example.roombooking.exception;

public class RoomAlreadyBookedException extends RuntimeException {
    public RoomAlreadyBookedException() {
        super("Переговорная уже забронирована для указанного периода.");
    }
}