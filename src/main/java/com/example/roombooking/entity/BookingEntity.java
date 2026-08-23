package com.example.roombooking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="Bookings")
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name="room_id")
    private RoomEntity room;

    @Column(name="start_at",nullable = false)
    private LocalDateTime startAt;

    @Column(name="start_at",nullable = false)
    private LocalDateTime endAt;

}
