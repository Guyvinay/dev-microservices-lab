package com.dev.service.impl;

import com.dev.dto.FormFieldDto;
import com.dev.entity.FormFieldEntity;
import com.dev.repository.FormFieldRepository;
import com.dev.service.FormFieldService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FormFieldServiceImpl implements FormFieldService {

    private final FormFieldRepository repository;

    public FormFieldServiceImpl(FormFieldRepository repository) {
        this.repository = repository;
    }

    @Override
    public FormFieldDto create(FormFieldDto dto) {

        FormFieldEntity entity = new FormFieldEntity();

        entity.setFormId(dto.getFormId());
        entity.setFieldId(dto.getFieldId());
        entity.setFieldOrder(dto.getFieldOrder());
        entity.setRequiredOverride(dto.getRequiredOverride());
        entity.setOverrideJson(dto.getOverrideJson());

        return map(repository.save(entity));
    }

    @Override
    public List<FormFieldDto> findByFormId(UUID formId) {

        return repository.findByFormIdOrderByFieldOrderAsc(formId)
                .stream()
                .map(this::map)
                .toList();
    }

    private FormFieldDto map(FormFieldEntity e) {

        FormFieldDto d = new FormFieldDto();

        d.setId(e.getId());
        d.setFormId(e.getFormId());
        d.setFieldId(e.getFieldId());
        d.setFieldOrder(e.getFieldOrder());
        d.setRequiredOverride(e.getRequiredOverride());
        d.setOverrideJson(e.getOverrideJson());

        return d;
    }
}
