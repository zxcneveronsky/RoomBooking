package com.example.roombooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.roombooking.entity.OptionEntity;

public interface OptionRepository extends JpaRepository<OptionEntity, Long> {
}
