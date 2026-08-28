package com.example.roombooking.exception;

public class OptionNotFoundException extends RuntimeException {
    public OptionNotFoundException(Long id) {
        super("Опция с id " + id + " не найдена.");
    }
}
