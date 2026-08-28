package com.example.roombooking.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UserNotFoundException.class, RoomNotFoundException.class, OptionNotFoundException.class})
    public ResponseEntity<Object> handleNotFound(RuntimeException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UserAlreadyExistsException.class, RoomAlreadyBookedException.class})
    public ResponseEntity<Object> handleAlreadyExists(RuntimeException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Object> handleInvalidPassword(InvalidPasswordException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        return buildResponse("Ошибка валидации: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildResponse("Ошибка валидации: " + errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Object> handleMethodValidation(HandlerMethodValidationException ex) {
        return buildResponse("Ошибка валидации: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleJsonParse(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();
        String message = cause != null ? cause.getMessage() : ex.getMessage();
        return buildResponse("Некорректный JSON: " + message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResource(NoResourceFoundException ex) {
        return buildResponse("Ресурс не найден.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return buildResponse("Метод не поддерживается для этого эндпоинта.", HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Object> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return buildResponse("Неподдерживаемый тип содержимого. Ожидается application/json.", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex) {
        return buildResponse("Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> buildResponse(String message, HttpStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }
}
