package com.example.roombooking.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Неверный пароль.");
    }
}
