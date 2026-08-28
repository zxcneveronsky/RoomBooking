package com.example.roombooking.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.roombooking.dto.request.CreateRoomRequest;
import com.example.roombooking.dto.request.UpdateRoomRequest;
import com.example.roombooking.dto.response.RoomResponse;
import com.example.roombooking.entity.RoomEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoomMapper {

    private final OptionMapper optionMapper;

    public RoomEntity toEntity(CreateRoomRequest request) {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setName(request.name());
        roomEntity.setCapacity(request.capacity());
        roomEntity.setFloor(request.floor());
        roomEntity.setDescription(request.description());
        return roomEntity;
    }

    public RoomEntity toEntity(UpdateRoomRequest request) {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setId(request.id());
        roomEntity.setName(request.name());
        roomEntity.setCapacity(request.capacity());
        roomEntity.setFloor(request.floor());
        roomEntity.setDescription(request.description());
        return roomEntity;
    }

    public RoomResponse toResponse(RoomEntity roomEntity) {
        return new RoomResponse(
                roomEntity.getId(),
                roomEntity.getName(),
                roomEntity.getCapacity(),
                roomEntity.getFloor(),
                roomEntity.getDescription(),
                roomEntity.getOptions() != null
                        ? roomEntity.getOptions().stream().map(optionMapper::toResponse).toList()
                        : List.of());
    }
}
