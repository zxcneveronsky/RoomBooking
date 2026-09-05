package com.example.roombooking.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.roombooking.entity.UserEntity;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    private JwtService jwtService;
    private UserEntity testUser;
    @BeforeEach
    void setUp(){
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "dGVzdC1zZWNyZXQta2V5LXRlc3Qtc2VjcmV0LWtleS0xMjM0");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 86400000L);

        testUser = new UserEntity();
        testUser.setEmail("test@mail.com");
        testUser.setRole(UserEntity.Role.USER);
    }

    @Test
    void should_generate_token(){
        String token = jwtService.generateToken(testUser);
        assertThat(token).isNotEmpty();
    }

    @Test
    void should_extract_username_from_token() {
        String token = jwtService.generateToken(testUser);
        String email = jwtService.extractUsername(token);
        assertThat(email).isEqualTo("test@mail.com");
    }

    @Test
    void should_validate_token_for_correct_user() {
        String token = jwtService.generateToken(testUser);
        boolean valid = jwtService.isTokenValid(token, new UserDetailsAdapter(testUser));
        assertThat(valid).isTrue();
    }

    @Test
    void should_reject_token_for_different_user() {
        String token = jwtService.generateToken(testUser);

        UserEntity otherUser = new UserEntity();
        otherUser.setEmail("other@mail.com");
        otherUser.setRole(UserEntity.Role.USER);

        boolean valid = jwtService.isTokenValid(token, new UserDetailsAdapter(otherUser));
        assertThat(valid).isFalse();
    }

    @Test
    void should_throw_exception_for_invalid_token() {
        assertThatThrownBy(() -> jwtService.extractUsername("bad.token.here"))
                .isInstanceOf(Exception.class);
    }
    
}
