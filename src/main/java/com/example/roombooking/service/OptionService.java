package com.example.roombooking.service;

import com.example.roombooking.mapper.OptionMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.roombooking.dto.request.CreateOptionRequest;
import com.example.roombooking.dto.response.OptionResponse;
import com.example.roombooking.entity.OptionEntity;
import com.example.roombooking.exception.OptionNotFoundException;
import com.example.roombooking.repository.OptionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OptionService {
    private final OptionMapper optionMapper;
    private final OptionRepository optionRepository;


    @Transactional(readOnly = true)
    public OptionEntity getById(Long id) {
        return optionRepository.findById(id)
                .orElseThrow(() -> new OptionNotFoundException(id));
    }

            

    @Transactional(readOnly = true)
    public Page<OptionEntity> getAll(Pageable pageable) {
        return optionRepository.findAll(pageable);
    }

    @Transactional
    public OptionEntity create(OptionEntity entity) {
        return optionRepository.save(entity);
    }


    @Transactional
    public OptionEntity update(OptionEntity entity) {
        Long id = entity.getId();
        return optionRepository.findById(id)
                .map(existing -> {
                    existing.setName(entity.getName());
                    return optionRepository.save(existing);
                })
                .orElseThrow(() -> new OptionNotFoundException(id));
    }



    @Transactional
    public void delete(Long id) {
        if (!optionRepository.existsById(id)) {
            throw new OptionNotFoundException(id);
        }
        optionRepository.deleteById(id);
    }
}
