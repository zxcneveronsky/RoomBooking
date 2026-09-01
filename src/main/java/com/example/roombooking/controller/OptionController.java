package com.example.roombooking.controller;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.roombooking.dto.request.CreateOptionRequest;
import com.example.roombooking.dto.request.UpdateOptionRequest;
import com.example.roombooking.dto.response.OptionResponse;
import com.example.roombooking.mapper.OptionMapper;
import com.example.roombooking.service.OptionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/options")
@RequiredArgsConstructor
@Validated
public class OptionController {
    private final OptionService optionService;
    private final OptionMapper optionMapper;

    @GetMapping
    public Page<OptionResponse> getAll(Pageable pageable) {
        return optionService.getAll(pageable).map(optionMapper::toResponse);
    }

    @GetMapping("/{id}")
    public OptionResponse getById(@PathVariable("id") Long id) {
        return optionMapper.toResponse(optionService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public OptionResponse create(@Valid @RequestBody CreateOptionRequest request) {
        return optionMapper.toResponse(optionService.create(optionMapper.toEntity(request)));
    }


    @PatchMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public OptionResponse update(@Valid @RequestBody UpdateOptionRequest request) {
        return optionMapper.toResponse(optionService.update(optionMapper.toEntity(request)));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        optionService.delete(id);
    }
    
}
