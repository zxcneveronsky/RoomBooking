package com.example.roombooking.mapper;

import org.springframework.stereotype.Component;

import com.example.roombooking.dto.request.CreateRoomRequest;
import com.example.roombooking.dto.request.UpdateRoomRequest;
import com.example.roombooking.dto.response.RoomResponse;
import com.example.roombooking.entity.RoomEntity;

@Component
public class RoomMapper {

    public RoomEntity toEntity(CreateRoomRequest request) {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setName(request.name());
        roomEntity.setCapacity(request.capacity());
        roomEntity.setDescription(request.description());
        return roomEntity;
    }

    public RoomEntity toEntity(UpdateRoomRequest request) {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setId(request.id());
        roomEntity.setName(request.name());
        roomEntity.setCapacity(request.capacity());
        roomEntity.setDescription(request.description());
        return roomEntity;
    }

    public RoomResponse toResponse(RoomEntity roomEntity) {
        return new RoomResponse(roomEntity.getId(), roomEntity.getName(),
                roomEntity.getCapacity(), roomEntity.getDescription());
    }
}
