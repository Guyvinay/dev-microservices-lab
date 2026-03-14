package com.dev.service;

import com.dev.dto.FieldSchema;
import com.dev.dto.FormPublishedEvent;
import com.dev.entity.FormFieldDefinition;
import com.dev.entity.SpaceForm;
import com.dev.repo.FieldDefinitionRepository;
import com.dev.repo.FormFieldDefinitionRepository;
import com.dev.repo.SpaceFormRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormPublishService {

    private final SpaceFormRepository formRepository;
    private final FormFieldDefinitionRepository fieldRepository;
    private final FieldDefinitionRepository definitionRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void publishForm(Long formId) {

        SpaceForm form = formRepository.findById(formId)
                .orElseThrow();

        List<FormFieldDefinition> formFields =
                fieldRepository.findByFormId(formId);

        List<FieldSchema> fields = formFields.stream()
                .map(this::buildFieldSchema)
                .toList();

        FormPublishedEvent event =
                FormPublishedEvent.builder()
                        .tenantId("T1")
                        .spaceId(form.getSpaceId())
                        .formId(form.getId())
                        .version(1)
                        .tableName(buildTableName(form))
                        .fields(fields)
                        .build();

        eventPublisher.publishEvent(event);

        form.setStatus("PUBLISHED");
        formRepository.save(form);
    }

    private String buildTableName(SpaceForm form) {
    }

    private Object buildFieldSchema(FormFieldDefinition formFieldDefinition) {


    }