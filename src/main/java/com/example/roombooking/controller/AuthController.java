package com.example.roombooking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.roombooking.dto.request.AuthRequest;
import com.example.roombooking.dto.response.AuthResponse;
import com.example.roombooking.exception.AccessDeniedException;
import com.example.roombooking.mapper.UserMapper;
import com.example.roombooking.security.UserDetailsAdapter;
import com.example.roombooking.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody AuthRequest request) {
        log.info("Зарегистрирован пользователь: {}", request.email());
        return userService.registerUser(userMapper.toEntity(request));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        log.info("Пользователь вошёл в систему: {}", request.email());
        return userService.loginUser(request.email(), request.password());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@AuthenticationPrincipal UserDetailsAdapter adapter,
                           @PathVariable("id") Long userId) {
        if (!adapter.getUserId().equals(userId)) {
            throw new AccessDeniedException();
        }
        log.info("Удалён пользователь: userId={}", userId);
        userService.deleteUser(userId);
    }
}
