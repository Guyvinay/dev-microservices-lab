package com.dev.service.impl;

import com.dev.dto.FormDto;
import com.dev.entity.FormEntity;
import com.dev.repository.FormRepository;
import com.dev.service.FormService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FormServiceImpl implements FormService {

    private final FormRepository repository;

    public FormServiceImpl(FormRepository repository) {
        this.repository = repository;
    }

    @Override
    public FormDto create(FormDto dto) {

        FormEntity entity = new FormEntity();

        entity.setSpaceId(dto.getSpaceId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setStatus("ACTIVE");
        entity.setCreatedAt(System.currentTimeMillis());

        return map(repository.save(entity));
    }

    @Override
    public FormDto getById(UUID uuid) {
        return map(
                repository.findById(uuid).orElseThrow(
                        ()-> new RuntimeException("Form not found")
                )
        );
    }

    @Override
    public void updateStatus(UUID formId, String published) {

    }

    private FormDto map(FormEntity e) {

        FormDto d = new FormDto();

        d.setId(e.getId());
        d.setSpaceId(e.getSpaceId());
        d.setName(e.getName());
        d.setDescription(e.getDescription());
        d.setStatus(e.getStatus());

        return d;
    }
}
