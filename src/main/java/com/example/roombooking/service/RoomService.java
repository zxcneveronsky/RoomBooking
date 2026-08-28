package com.example.roombooking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.roombooking.entity.OptionEntity;
import com.example.roombooking.entity.RoomEntity;
import com.example.roombooking.exception.OptionNotFoundException;
import com.example.roombooking.exception.RoomNotFoundException;
import com.example.roombooking.repository.OptionRepository;
import com.example.roombooking.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final OptionRepository optionRepository;

    @Transactional(readOnly = true)
    public Page<RoomEntity> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public RoomEntity getRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
    }

    @Transactional(readOnly = true)
    public Page<RoomEntity> getAvailableRooms(int requiredCapacity, LocalDateTime startFrom, LocalDateTime endTo, Pageable pageable) {
        return roomRepository.findAvailableByCapacityAndPeriod(requiredCapacity, startFrom, endTo, pageable);
    }

    @Transactional
    public RoomEntity createRoom(RoomEntity roomEntity, List<Long> optionIds) {
        List<Long> distinctOptionIds = optionIds.stream().distinct().toList();
        List<OptionEntity> options = optionRepository.findAllById(distinctOptionIds);
        if (options.size() != distinctOptionIds.size()) {
            List<Long> foundOptionIds = options.stream().map(OptionEntity::getId).toList();
            Long missingOptionId = distinctOptionIds.stream()
                    .filter(id -> !foundOptionIds.contains(id))
                    .findFirst()
                    .orElse(distinctOptionIds.getFirst());
            throw new OptionNotFoundException(missingOptionId);
        }
        roomEntity.setOptions(options);
        return roomRepository.save(roomEntity);
    }

    @Transactional
    public RoomEntity updateRoom(RoomEntity roomUpdate, List<Long> optionIds) {
        Long roomId = roomUpdate.getId();
        RoomEntity updatedRoom = roomRepository.findById(roomId)
                .map(existingRoom -> {
                    existingRoom.setName(roomUpdate.getName() != null ? roomUpdate.getName() : existingRoom.getName());
                    existingRoom.setDescription(roomUpdate.getDescription() != null ? roomUpdate.getDescription() : existingRoom.getDescription());
                    existingRoom.setCapacity(roomUpdate.getCapacity() != null ? roomUpdate.getCapacity() : existingRoom.getCapacity());
                    existingRoom.setFloor(roomUpdate.getFloor() != null ? roomUpdate.getFloor() : existingRoom.getFloor());
                    if (optionIds != null) {
                        if (optionIds.isEmpty()) {
                            existingRoom.setOptions(List.of());
                        } else {
                            List<Long> distinctOptionIds = optionIds.stream().distinct().toList();
                            List<OptionEntity> options = optionRepository.findAllById(distinctOptionIds);
                            if (options.size() != distinctOptionIds.size()) {
                                List<Long> foundOptionIds = options.stream().map(OptionEntity::getId).toList();
                                Long missingOptionId = distinctOptionIds.stream()
                                        .filter(id -> !foundOptionIds.contains(id))
                                        .findFirst()
                                        .orElse(distinctOptionIds.getFirst());
                                throw new OptionNotFoundException(missingOptionId);
                            }
                            existingRoom.setOptions(options);
                        }
                    }
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
