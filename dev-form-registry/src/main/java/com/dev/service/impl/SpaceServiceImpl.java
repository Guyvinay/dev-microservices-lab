package com.dev.service.impl;

import com.dev.dto.SpaceDto;
import com.dev.entity.SpaceEntity;
import com.dev.repository.SpaceRepository;
import com.dev.service.SpaceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SpaceServiceImpl implements SpaceService {

    private final SpaceRepository repository;

    public SpaceServiceImpl(SpaceRepository repository) {
        this.repository = repository;
    }

    @Override
    public SpaceDto create(SpaceDto dto) {

        SpaceEntity entity = new SpaceEntity();

        entity.setId(UUID.randomUUID());
        entity.setTenantId(dto.getTenantId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCreatedAt(System.currentTimeMillis());
        entity.setCreatedBy(dto.getCreatedBy());

        SpaceEntity saved = repository.save(entity);

        return mapToDto(saved);
    }

    @Override
    public SpaceDto getById(UUID id) {

        SpaceEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Space not found"));

        return mapToDto(entity);
    }

    @Override
    public List<SpaceDto> getByTenant(String tenantId) {

        return repository.findByTenantId(tenantId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private SpaceDto mapToDto(SpaceEntity entity) {

        SpaceDto dto = new SpaceDto();

        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenantId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());

        return dto;
    }
}