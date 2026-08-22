package com.example.roombooking.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.roombooking.dto.request.CreateRoomRequest;
import com.example.roombooking.dto.request.UpdateRoomRequest;
import com.example.roombooking.dto.response.RoomResponse;
import com.example.roombooking.mapper.RoomMapper;
import com.example.roombooking.service.RoomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Validated
public class RoomController {

    private final RoomService roomService;
    private final RoomMapper roomMapper;

    @GetMapping
    public Page<RoomResponse> getAllRooms(Pageable pageable) {
        return roomService.getAllRooms(pageable).map(roomMapper::toResponse);
    }

    @GetMapping("/{id}")
    public RoomResponse getRoomById(@PathVariable("id") Long roomId) {
        return roomMapper.toResponse(roomService.getRoomById(roomId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse createRoom(@Valid @RequestBody CreateRoomRequest request) {
        return roomMapper.toResponse(roomService.createRoom(roomMapper.toEntity(request)));
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public RoomResponse updateRoom(@Valid @RequestBody UpdateRoomRequest request) {
        return roomMapper.toResponse(roomService.updateRoom(roomMapper.toEntity(request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable("id") Long roomId) {
        roomService.deleteRoom(roomId);
    }
}
