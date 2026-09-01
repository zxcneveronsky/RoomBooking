package com.example.roombooking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.roombooking.dto.response.UserResponse;

import com.example.roombooking.entity.UserEntity;
import com.example.roombooking.exception.UserNotFoundException;
import com.example.roombooking.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserRepository userRepository;

    @GetMapping("/users")
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(u -> new UserResponse(u.getId(), u.getEmail(), u.getRole().name()));
    }

    @PatchMapping("/users/{id}/role")
    @ResponseStatus(HttpStatus.OK)
    public void setRole(@PathVariable Long id, @RequestParam String role) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setRole(UserEntity.Role.valueOf(role));
        userRepository.save(user);
    }
}