package com.example.roombooking.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.roombooking.dto.request.CreateRoomRequest;
import com.example.roombooking.dto.request.UpdateRoomRequest;
import com.example.roombooking.dto.response.BookingResponse;
import com.example.roombooking.dto.response.RoomResponse;
import com.example.roombooking.mapper.BookingMapper;
import com.example.roombooking.mapper.RoomMapper;
import com.example.roombooking.service.BookingService;
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
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @GetMapping
    public Page<RoomResponse> searchRooms(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) List<Long> optionIds,
            Pageable pageable) {
        return roomService.searchRooms(name, capacity, floor, optionIds, pageable)
                .map(roomMapper::toResponse);
    }

    @GetMapping("/{id}")
    public RoomResponse getRoomById(@PathVariable("id") Long roomId) {
        return roomMapper.toResponse(roomService.getRoomById(roomId));
    }

    @GetMapping("/{id}/bookings")
    public List<BookingResponse> getRoomBookings(
            @PathVariable("id") Long roomId,
            @RequestParam("date") LocalDate date) {
        return bookingService.getRoomSchedule(roomId, date)
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @GetMapping("/available")
    public Page<RoomResponse> getAvailableRooms(
            @RequestParam("requiredCapacity") Integer requiredCapacity,
            @RequestParam("startFrom") LocalDateTime startFrom,
            @RequestParam("endTo") LocalDateTime endTo,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) List<Long> optionIds,
            Pageable pageable) {
        return roomService.searchAvailableRooms(name, requiredCapacity, floor, optionIds, startFrom, endTo, pageable)
                .map(roomMapper::toResponse);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse createRoom(@Valid @RequestBody CreateRoomRequest request) {
        return roomMapper.toResponse(roomService.createRoom(roomMapper.toEntity(request), request.optionIds()));
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public RoomResponse updateRoom(@Valid @RequestBody UpdateRoomRequest request) {
        return roomMapper.toResponse(roomService.updateRoom(roomMapper.toEntity(request), request.optionIds()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable("id") Long roomId) {
        roomService.deleteRoom(roomId);
    }
}
