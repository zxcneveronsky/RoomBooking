package com.example.roombooking.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("Доступ запрещен.");
    }
}
