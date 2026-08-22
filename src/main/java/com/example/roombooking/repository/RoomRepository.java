package com.example.roombooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.roombooking.entity.RoomEntity;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
}
