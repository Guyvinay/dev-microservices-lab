package com.dev.service.impl;

import com.dev.dto.FieldDefinitionDto;
import com.dev.dto.FormDto;
import com.dev.dto.FormFieldDto;
import com.dev.dto.SpaceDto;
import com.dev.dto.ValidationConfig;
import com.dev.jooq.definition.DynamicColumnDefinition;
import com.dev.jooq.definition.DynamicDataType;
import com.dev.jooq.definition.DynamicTableDefinition;
import com.dev.jooq.utility.DynamicNamingStrategy;
import com.dev.jooq.utility.DynamicSchemaService;
import com.dev.service.FieldDefinitionService;
import com.dev.service.FormFieldService;
import com.dev.service.FormPublishService;
import com.dev.service.FormService;
import com.dev.service.SpaceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FormPublishServiceImpl implements FormPublishService {

    private final FormService formService;
    private final FormFieldService formFieldService;
    private final FieldDefinitionService fieldDefinitionService;
    private final DynamicSchemaService dynamicSchemaService;
    private final DynamicNamingStrategy namingStrategy;
    private final ObjectMapper objectMapper;
    private final SpaceService spaceService;

    @Transactional
    @Override
    public void publishForm(UUID formId) {

        FormDto form = formService.getById(formId);

        if ("PUBLISHED".equals(form.getStatus())) {
            return;
        }

        List<FormFieldDto> formFields =
                formFieldService.findByFormId(formId);

        if (formFields.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot publish empty form: " + formId
            );
        }


        List<DynamicColumnDefinition> columns =
                formFields.stream()
                        .map(this::resolveColumnDefinition)
                        .toList();

        String tenantSchema = resolveTenantSchema(form.getSpaceId());

        String tableName = namingStrategy
                .normalizeTableName(form.getName());

        DynamicTableDefinition tableDefinition =
                DynamicTableDefinition.builder()
                        .schema(tenantSchema)
                        .tableName(tableName)
                        .columns(columns)
                        .includeAuditColumns(true)
                        .build();

        log.info("Publishing form: {}", formId);

        dynamicSchemaService.syncTable(tableDefinition);

        log.info("Dynamic table ready: {}.{}",
                tenantSchema,
                tableName);

        formService.updateStatus(formId, "PUBLISHED");

        log.info("Form marked PUBLISHED: {}", formId);

    }

    private DynamicColumnDefinition resolveColumnDefinition(FormFieldDto formField) {

        try {

            FieldDefinitionDto baseField = null;

            if (formField.getOverrideJson() == null) {

                baseField = fieldDefinitionService
                        .getById(formField.getFieldId());
            }

            String columnName =
                    namingStrategy.normalizeColumnName(
                            baseField != null
                                    ? baseField.getLabel()
                                    : extractLabelFromOverride(formField)
                    );

            ValidationConfig validation =
                    extractValidation(baseField, formField);

            return DynamicColumnDefinition.builder()
                    .name(columnName)
                    .dataType(resolveDataType(baseField, formField))
                    .length(resolveLength(validation))
                    .nullable(!Boolean.TRUE.equals(validation.getRequired()))
                    .unique(false)
                    .build();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to resolve column for field: "
                            + formField.getId(),
                    e
            );
        }
    }

    private ValidationConfig extractValidation(FieldDefinitionDto base,
                                               FormFieldDto formField)
            throws JsonProcessingException {

        if (formField.getOverrideJson() != null) {

            JsonNode root = objectMapper.readTree(
                    formField.getOverrideJson()
            );

            JsonNode validationNode = root.get("validation");

            return objectMapper.treeToValue(
                    validationNode,
                    ValidationConfig.class
            );
        }

        return objectMapper.readValue(
                base.getValidationJson(),
                ValidationConfig.class
        );
    }

    private DynamicDataType resolveDataType(FieldDefinitionDto base,
                                            FormFieldDto formField)
            throws JsonProcessingException {

        if (formField.getOverrideJson() != null) {

            JsonNode root = objectMapper.readTree(
                    formField.getOverrideJson());

            return DynamicDataType.valueOf(
                    root.get("data_type").asText()
            );
        }

        return DynamicDataType.valueOf(base.getDataType());
    }

    private Integer resolveLength(ValidationConfig validation) {

        if (validation.getMaxLength() != null) {
            return validation.getMaxLength();
        }

        return 255;
    }


    private String resolveTenantSchema(UUID spaceId) {

        SpaceDto space = spaceService.getById(spaceId);

        return space.getTenantId();
    }

    private String extractLabelFromOverride(FormFieldDto formField)
            throws JsonProcessingException {

        JsonNode root = objectMapper.readTree(formField.getOverrideJson());

        JsonNode labelNode = root.get("label");

        if (labelNode == null || labelNode.asText().isBlank()) {
            throw new IllegalStateException(
                    "Override JSON missing label for form_field: "
                            + formField.getId()
            );
        }

        return labelNode.asText();
    }
}
