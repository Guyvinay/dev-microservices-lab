package com.dev.service;

import com.dev.dto.SpaceDto;

import java.util.List;
import java.util.UUID;

public interface SpaceService {
    SpaceDto create(SpaceDto dto);

    SpaceDto getById(UUID id);

    List<SpaceDto> getByTenant(String tenantId);
}
