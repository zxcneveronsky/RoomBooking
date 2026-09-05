package com.example.roombooking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.example.roombooking.dto.response.AuthResponse;
import com.example.roombooking.entity.UserEntity;
import com.example.roombooking.exception.InvalidPasswordException;
import com.example.roombooking.exception.UserAlreadyExistsException;
import com.example.roombooking.exception.UserNotFoundException;
import com.example.roombooking.repository.UserRepository;
import com.example.roombooking.security.JwtService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void should_login_user_with_correct_password(){
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setPassword("encoded_pass");
        user.setRole(UserEntity.Role.USER);

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("raw_pass", "encoded_pass")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = userService.loginUser("test@mail.com", "raw_pass");

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.email()).isEqualTo("test@mail.com");
    }

    @Test
    void should_throw_when_email_not_found(){
        when(userRepository.findByEmail("unknown@mail.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.loginUser("unknown@mail.com", "pass"))
        .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void should_throw_when_password_wrong() {
        UserEntity user = new UserEntity();
        user.setEmail("test@mail.com");
        user.setPassword("encoded_pass");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded_pass")).thenReturn(false);

        assertThatThrownBy(() -> userService.loginUser("test@mail.com", "wrong"))
            .isInstanceOf(InvalidPasswordException.class);
    }



    @Test
    void should_register_new_user() {
        UserEntity user = new UserEntity();
        user.setEmail("new@mail.com");
        user.setPassword("raw_pass");
        user.setRole(UserEntity.Role.USER);

        when(userRepository.existsByEmail("new@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("raw_pass")).thenReturn("encoded_pass");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> {
            UserEntity saved = inv.getArgument(0);
            saved.setId(1L);
            return saved;});
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        AuthResponse response = userService.registerUser(user);

        assertThat(response.email()).isEqualTo("new@mail.com");
        assertThat(response.token()).isEqualTo("jwt-token");
        verify(userRepository).save(user);
    }

    @Test
        void should_throw_when_email_already_exists() {
        UserEntity user = new UserEntity();
        user.setEmail("taken@mail.com");
        user.setPassword("pass");

        when(userRepository.existsByEmail("taken@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(user))
            .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
        void should_delete_existing_user() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
        void should_throw_when_deleting_nonexistent_user() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(99L))
            .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).deleteById(any());
    }

}
