package com.example.roombooking.mapper;

import org.springframework.stereotype.Component;

import com.example.roombooking.dto.request.CreateOptionRequest;
import com.example.roombooking.dto.request.UpdateOptionRequest;
import com.example.roombooking.dto.response.OptionResponse;
import com.example.roombooking.entity.OptionEntity;

@Component
public class OptionMapper {

    public OptionEntity toEntity(CreateOptionRequest request) {
        OptionEntity optionEntity = new OptionEntity();
        optionEntity.setName(request.name());
        return optionEntity;
    }

    public OptionEntity toEntity(UpdateOptionRequest request) {
        OptionEntity optionEntity = new OptionEntity();
        optionEntity.setId(request.id());
        optionEntity.setName(request.name());
        return optionEntity;
    }

    public OptionResponse toResponse(OptionEntity optionEntity) {
        return new OptionResponse(optionEntity.getId(), optionEntity.getName());
    }
}
