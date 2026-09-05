package com.example.roombooking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import java.util.List;
import com.example.roombooking.entity.OptionEntity;
import com.example.roombooking.exception.OptionNotFoundException;
import com.example.roombooking.mapper.OptionMapper;
import com.example.roombooking.repository.OptionRepository;

import java.util.List;
import java.util.Optional;
@ExtendWith(MockitoExtension.class)
public class OptionServiceTest {
    @Mock
    private OptionRepository optionRepository;

    @Mock
    private OptionMapper optionMapper;

    @InjectMocks
    private OptionService optionService;

    @Test
    void should_return_option_when_found(){
        OptionEntity option = new OptionEntity();
        option.setId(1L);
        option.setName("test");

        when(optionRepository.findById(1L)).thenReturn(Optional.of(option));

        OptionEntity result = optionService.getById(1L);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("test");
        verify(optionRepository).findById(1L);
    }

    @Test
    void should_throw_when_option_not_found(){
        when(optionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(()->optionService.getById(99L)).isInstanceOf(OptionNotFoundException.class);
    }

    @Test
    void should_return_page_of_options(){
        Page<OptionEntity> page = new PageImpl<>(List.of(new OptionEntity(),new OptionEntity()));

        when(optionRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<OptionEntity> result = optionService.getAll(Pageable.ofSize(10));

        assertThat(result).hasSize(2);

    }

    @Test
    void should_create_option() {
        OptionEntity option = new OptionEntity();
        option.setName("test");

        when(optionRepository.save(any(OptionEntity.class))).thenReturn(option);

        OptionEntity result = optionService.create(option);

        assertThat(result.getName()).isEqualTo("test");
        verify(optionRepository).save(option);
    }

    @Test
    void should_update_option_when_found() {
        OptionEntity existing = new OptionEntity();
        existing.setId(1L);
        existing.setName("Старое имя");

        OptionEntity updated = new OptionEntity();
        updated.setId(1L);
        updated.setName("Новое имя");

        when(optionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(optionRepository.save(any())).thenReturn(existing);

        OptionEntity result = optionService.update(updated);

        assertThat(result.getName()).isEqualTo("Новое имя");
        verify(optionRepository).save(existing);
    }

    @Test
    void should_throw_when_updating_nonexistent_option() {
        OptionEntity option = new OptionEntity();
        option.setId(99L);
        option.setName("Нет");

        when(optionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> optionService.update(option))
                .isInstanceOf(OptionNotFoundException.class);
    }

    @Test
    void should_delete_option_when_exists() {
        when(optionRepository.existsById(1L)).thenReturn(true);

        optionService.delete(1L);

        verify(optionRepository).deleteById(1L);
    }

    @Test
    void should_throw_when_deleting_nonexistent_option() {
        when(optionRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> optionService.delete(99L))
                .isInstanceOf(OptionNotFoundException.class);

        verify(optionRepository, never()).deleteById(any());
    }
}
