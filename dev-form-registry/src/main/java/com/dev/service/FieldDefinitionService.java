package com.dev.service;

import com.dev.dto.FieldDefinitionDto;

import java.util.List;
import java.util.UUID;

public interface FieldDefinitionService {

    FieldDefinitionDto create(FieldDefinitionDto dto);

    List<FieldDefinitionDto> getBySpace(UUID spaceId);
}