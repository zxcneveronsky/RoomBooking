package com.example.roombooking.mapper;

import org.springframework.stereotype.Component;

import com.example.roombooking.dto.request.AuthRequest;
import com.example.roombooking.entity.UserEntity;
import com.example.roombooking.entity.UserEntity.Role;

@Component
public class UserMapper {

    public UserEntity toEntity(AuthRequest request) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.email());
        userEntity.setPassword(request.password());
        userEntity.setRole(Role.USER);
        return userEntity;
    }
}
