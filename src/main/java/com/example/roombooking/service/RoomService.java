package com.example.roombooking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.roombooking.entity.RoomEntity;
import com.example.roombooking.exception.RoomNotFoundException;
import com.example.roombooking.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public Page<RoomEntity> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public RoomEntity getRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
    }

    @Transactional
    public RoomEntity createRoom(RoomEntity roomEntity) {
        return roomRepository.save(roomEntity);
    }

    @Transactional
    public RoomEntity updateRoom(RoomEntity roomUpdate) {
        Long roomId = roomUpdate.getId();
        RoomEntity updatedRoom = roomRepository.findById(roomId)
                .map(existingRoom -> {
                    existingRoom.setName(roomUpdate.getName() != null ? roomUpdate.getName() : existingRoom.getName());
                    existingRoom.setDescription(roomUpdate.getDescription() != null ? roomUpdate.getDescription() : existingRoom.getDescription());
                    existingRoom.setCapacity(roomUpdate.getCapacity() != null ? roomUpdate.getCapacity() : existingRoom.getCapacity());
                    return roomRepository.save(existingRoom);
                })
                .orElseThrow(() -> new RoomNotFoundException(roomId));
        return updatedRoom;
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new RoomNotFoundException(roomId);
        }
        roomRepository.deleteById(roomId);
    }
}
