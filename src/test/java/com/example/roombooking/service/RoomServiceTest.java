package com.example.roombooking.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.List;
import com.example.roombooking.repository.OptionRepository;
import java.util.Optional;

import com.example.roombooking.entity.OptionEntity;
import com.example.roombooking.entity.RoomEntity;
import com.example.roombooking.exception.OptionNotFoundException;
import com.example.roombooking.exception.RoomNotFoundException;
import com.example.roombooking.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private OptionRepository optionRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    void should_return_room_when_found(){
        RoomEntity room = new RoomEntity();
        room.setName("test");
        room.setId(1L);
        room.setCapacity(10);
        room.setDescription("BigRoom");
        room.setFloor(1);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        RoomEntity result = roomService.getRoomById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("test");

    }

    @Test
        void should_throw_when_room_not_found() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.getRoomById(99L))
            .isInstanceOf(RoomNotFoundException.class);
    }

    @Test
        void should_return_page_of_rooms() {
        Page<RoomEntity> page = new PageImpl<>(List.of(new RoomEntity(), new RoomEntity()));
        when(roomRepository.searchRoom(any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        Page<RoomEntity> result = roomService.searchRooms(null, null, null, null, Pageable.ofSize(10));

        assertThat(result).hasSize(2);
    }

    @Test
        void should_return_available_rooms() {
        Page<RoomEntity> page = new PageImpl<>(List.of(new RoomEntity()));
        when(roomRepository.searchAvailableRoom(any(), any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        Page<RoomEntity> result = roomService.searchAvailableRooms(null, null, null, null, LocalDateTime.now(), LocalDateTime.now().plusHours(2), Pageable.ofSize(10));

        assertThat(result).hasSize(1);
    }

    @Test
    void should_create_room_with_valid_options() {
        RoomEntity room = new RoomEntity();
        room.setName("Зал 2");
        room.setCapacity(5);
        room.setFloor(2);
        room.setDescription("Малый зал");

        OptionEntity option = new OptionEntity();
        option.setId(1L);
        option.setName("Проектор");

        when(optionRepository.findAllById(List.of(1L))).thenReturn(List.of(option));
        when(roomRepository.save(any())).thenReturn(room);

        RoomEntity result = roomService.createRoom(room, List.of(1L));

        assertThat(result.getName()).isEqualTo("Зал 2");
        verify(roomRepository).save(room);
    }

    @Test
        void should_throw_when_option_not_found() {
        RoomEntity room = new RoomEntity();

        when(optionRepository.findAllById(List.of(99L))).thenReturn(List.of());

        assertThatThrownBy(() -> roomService.createRoom(room, List.of(99L)))
            .isInstanceOf(OptionNotFoundException.class);
    }

    @Test
        void should_update_room() {
        RoomEntity existing = new RoomEntity();
        existing.setId(1L);
        existing.setName("Старое");
        existing.setCapacity(5);
        existing.setFloor(1);
        existing.setDescription("Старое описание");

        RoomEntity update = new RoomEntity();
        update.setId(1L);
        update.setName("Новое");
        update.setCapacity(10);
        update.setFloor(2);
        update.setDescription("Новое описание");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(roomRepository.save(any())).thenReturn(existing);

        RoomEntity result = roomService.updateRoom(update, null);

        assertThat(result.getName()).isEqualTo("Новое");
        verify(roomRepository).save(existing);
    }

    @Test
        void should_delete_room_when_exists() {
        when(roomRepository.existsById(1L)).thenReturn(true);

        roomService.deleteRoom(1L);

        verify(roomRepository).deleteById(1L);
    }

    @Test
        void should_throw_when_room_not_found_for_delete() {
        when(roomRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> roomService.deleteRoom(99L))
            .isInstanceOf(RoomNotFoundException.class);

        verify(roomRepository, never()).deleteById(any());
    }
    
}
