package com.dev.service.impl;

import com.dev.dto.FieldDefinitionDto;
import com.dev.entity.FieldDefinitionEntity;
import com.dev.repository.FieldDefinitionRepository;
import com.dev.service.FieldDefinitionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FieldDefinitionServiceImpl implements FieldDefinitionService {

    private final FieldDefinitionRepository repository;

    public FieldDefinitionServiceImpl(FieldDefinitionRepository repository) {
        this.repository = repository;
    }

    @Override
    public FieldDefinitionDto create(FieldDefinitionDto dto) {

        FieldDefinitionEntity entity = new FieldDefinitionEntity();

        entity.setSpaceId(dto.getSpaceId());
        entity.setType(dto.getType());
        entity.setLabel(dto.getLabel());
        entity.setDataType(dto.getDataType());
        entity.setUiJson(dto.getUiJson());
        entity.setValidationJson(dto.getValidationJson());
        entity.setStatus("ACTIVE");
        entity.setCreatedAt(System.currentTimeMillis());

        return map(repository.save(entity));
    }

    @Override
    public List<FieldDefinitionDto> getBySpace(UUID spaceId) {

        return repository.findBySpaceId(spaceId)
                .stream()
                .map(this::map)
                .toList();
    }

    private FieldDefinitionDto map(FieldDefinitionEntity e) {

        FieldDefinitionDto d = new FieldDefinitionDto();

        d.setId(e.getId());
        d.setSpaceId(e.getSpaceId());
        d.setType(e.getType());
        d.setLabel(e.getLabel());
        d.setDataType(e.getDataType());
        d.setUiJson(e.getUiJson());
        d.setValidationJson(e.getValidationJson());
        d.setStatus(e.getStatus());

        return d;
    }
}
