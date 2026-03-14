package com.dev.service;

import com.dev.dto.FieldSchema;
import com.dev.dto.FormFieldWithDefinition;
import com.dev.dto.FormPublishedEvent;
import com.dev.entity.FieldDefinition;
import com.dev.entity.FormFieldDefinition;
import com.dev.entity.SpaceForm;
import com.dev.grpc.client.RecordSchemaGrpcService;
import com.dev.repo.FormFieldDefinitionRepository;
import com.dev.repo.SpaceFormRepository;
import com.dev.utility.grpc.form.CreateTableRequest;
import com.dev.utility.grpc.form.CreateTableResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FormPublishService {

    private final SpaceFormRepository formRepository;
    private final FormFieldDefinitionRepository fieldRepository;
    private final RecordSchemaGrpcService recordSchemaGrpcService;

    @Transactional
    public void publishForm(Long formId) {

        log.info("Publishing form with id={}", formId);

        SpaceForm form = formRepository.findById(formId)
                .orElseThrow(() -> new NoSuchElementException("Form not found: " + formId));

        List<FormFieldWithDefinition> fields = fieldRepository.findFields(formId);

        List<FieldSchema> schemas = fields.stream()
                .map(f -> buildFieldSchema(f.formField(), f.definition()))
                .toList();

        FormPublishedEvent event = FormPublishedEvent.builder()
                .spaceId(form.getSpaceId())
                .formId(form.getId())
                .tableName(buildTableName(form))
                .fields(schemas)
                .build();

        log.debug("Form schema prepared with {} fields", schemas.size());

        // Convert to gRPC proto
        CreateTableRequest request = toProto(event);

        // Call gRPC service
        CreateTableResponse response = recordSchemaGrpcService.sendCreateTableRequestEvent(request);

        if (!response.getSuccess()) {
            log.error("Table creation failed for formId={} : {}", formId, response.getMessage());
            throw new IllegalStateException("Table creation failed: " + response.getMessage());
        }

        form.setStatus("PUBLISHED");
        formRepository.save(form);

        log.info("Form published successfully with id={} and tableName={}", formId, event.getTableName());
    }

    /**
     * Converts your internal FormPublishedEvent -> gRPC CreateTableRequest
     */
    public CreateTableRequest toProto(FormPublishedEvent event) {
        log.debug("Converting FormPublishedEvent to gRPC CreateTableRequest for formId={}", event.getFormId());

        List<com.dev.utility.grpc.form.FieldSchema> protoFields = event.getFields().stream()
                .map(this::toProto)
                .toList();

        return CreateTableRequest.newBuilder()
                .setSpaceId(event.getSpaceId())
                .setFormId(event.getFormId())
                .setTableName(event.getTableName())
                .addAllFields(protoFields)
                .build();
    }

    /**
     * Converts internal FieldSchema -> gRPC FieldSchema
     */
    private com.dev.utility.grpc.form.FieldSchema toProto(FieldSchema schema) {
        return com.dev.utility.grpc.form.FieldSchema.newBuilder()
                .setName(schema.getName())
                .setType(schema.getType())
                .setNullable(schema.isNullable())
                .setUnique(schema.isUnique())
                .setIndexed(schema.isIndexed())
                .build();
    }

    private FieldSchema buildFieldSchema(
            FormFieldDefinition ffd,
            FieldDefinition fd) {

        Map<String, Object> db = merge(fd.getDb(), ffd.getDbOverride());

        boolean nullable = !Boolean.TRUE.equals(db.get("required"));
        boolean unique = Boolean.TRUE.equals(db.get("unique"));
        boolean indexed = Boolean.TRUE.equals(db.get("indexed"));

        return FieldSchema.builder()
                .name(resolveColumnName(ffd, fd))
                .type(resolveType(fd))
                .nullable(nullable)
                .unique(unique)
                .indexed(indexed)
                .build();
    }

    private Map<String,Object> merge(
            Map<String,Object> base,
            Map<String,Object> override) {

        Map<String,Object> result = new HashMap<>();

        if (base != null)
            result.putAll(base);

        if (override != null)
            result.putAll(override);

        return result;
    }

    private String resolveColumnName(
            FormFieldDefinition ffd,
            FieldDefinition fd) {

        String label = Optional.ofNullable(ffd.getLabelOverride())
                .orElse(fd.getLabel());

        return label.toLowerCase()
                .replaceAll("[^a-z0-9]", "_")
                .replaceAll("_+", "_");
    }
    private String resolveType(FieldDefinition fd) {

        return switch (fd.getFieldTypeId()) {
            case TEXT -> "VARCHAR(500)";
            case EMAIL -> "VARCHAR(255)";
            case NUMBER -> "BIGINT";
            case DATE -> "TIMESTAMP";
            case TIME -> null;
            case DATETIME -> null;
            case SELECT -> null;
            case CHECKBOX -> null;
            case RADIO -> null;
            case TEXTAREA -> null;
            case FILE -> null;
        };
    }
    private String toColumnName(FormFieldDefinition field) {

        String label = field.getLabelOverride();

        return label
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "_")
                .replaceAll("_+", "_");
    }

    private String buildTableName(SpaceForm form) {

        return "records_t" +
                "1_s" +
                form.getSpaceId() +
                "_f" +
                form.getId();
    }
}