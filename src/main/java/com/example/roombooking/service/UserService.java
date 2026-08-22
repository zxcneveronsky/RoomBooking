package com.example.roombooking.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.roombooking.dto.response.AuthResponse;
import com.example.roombooking.entity.UserEntity;
import com.example.roombooking.exception.InvalidPasswordException;
import com.example.roombooking.exception.UserAlreadyExistsException;
import com.example.roombooking.exception.UserNotFoundException;
import com.example.roombooking.repository.UserRepository;
import com.example.roombooking.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public AuthResponse loginUser(String email, String password) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new InvalidPasswordException();
        }
        String token = jwtService.generateToken(userEntity);
        return new AuthResponse(token, userEntity.getEmail(), userEntity.getRole().name());
    }

    @Transactional
    public AuthResponse registerUser(UserEntity userEntity) {
        if (userRepository.existsByEmail(userEntity.getEmail())) {
            throw new UserAlreadyExistsException(userEntity.getEmail());
        }
        userEntity.setId(null);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        UserEntity savedUserEntity = userRepository.save(userEntity);
        String token = jwtService.generateToken(savedUserEntity);
        return new AuthResponse(token, savedUserEntity.getEmail(), savedUserEntity.getRole().name());
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        userRepository.deleteById(userId);
    }
}
