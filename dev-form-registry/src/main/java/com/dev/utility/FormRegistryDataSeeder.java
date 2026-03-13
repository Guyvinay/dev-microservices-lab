package com.dev.utility;

import com.dev.entity.*;
import com.dev.repo.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class FormRegistryDataSeeder implements ApplicationRunner {

    private final FieldTypeRepository fieldTypeRepository;
    private final SpaceRepository spaceRepository;
    private final FieldDefinitionRepository fieldDefinitionRepository;
    private final SpaceFormRepository spaceFormRepository;
    private final FormFieldDefinitionRepository formFieldDefinitionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(ApplicationArguments args) throws JsonProcessingException {
        log.info("Starting default data seeding...");

        seedFieldTypes();
        Space hrSpace = seedSpaces();
        FieldDefinition emailField = seedFieldDefinitions(hrSpace);
        SpaceForm jobForm = seedForms(hrSpace);
        seedFormFieldDefinitions(jobForm, emailField);

        log.info("Default data seeding completed.");
    }


    private void seedFieldTypes() {
        for (FieldTypeEnum type : FieldTypeEnum.values()) {
            fieldTypeRepository.findById(type).orElseGet(() -> {
                FieldType ft = FieldType.builder()
                        .id(type)
                        .createdAt(Instant.now())
                        .build();
                log.info("FieldType {} {}", type, "created");
                return fieldTypeRepository.save(ft);
            });
        }
    }

    private Space seedSpaces() {
        return spaceRepository.findByName("HR Forms").orElseGet(() -> {
            Space s = Space.builder()
                    .name("HR Forms")
                    .description("All HR related forms")
                    .createdAt(Instant.now())
                    .build();
            Space saved = spaceRepository.save(s);
            log.info("Space '{}' created with id {}", saved.getName(), saved.getId());
            return saved;
        });
    }

    private FieldDefinition seedFieldDefinitions(Space space) {
        // Email field example
        return fieldDefinitionRepository.findByLabelAndSpaceId("Email Address", space.getId())
                .orElseGet(() -> {
                    Map<String, Object> ui = new HashMap<>();
                    ui.put("component", "input");
                    ui.put("placeholder", "example@company.com");
                    ui.put("width", 12);
                    ui.put("icon", "email");

                    Map<String, Object> validation = new HashMap<>();
                    validation.put("required", true);
                    validation.put("regex", "^[A-Za-z0-9+_.-]+@(.+)$");
                    validation.put("minLength", 5);
                    validation.put("maxLength", 100);

                    FieldDefinition field = FieldDefinition.builder()
                            .spaceId(space.getId())
                            .fieldTypeId(FieldTypeEnum.EMAIL)
                            .label("Email Address")
                            .description("Primary contact email")
                            .ui(ui)
                            .validation(validation)
                            .createdAt(Instant.now())
                            .build();
                    return fieldDefinitionRepository.save(field);
                });
    }

    private SpaceForm seedForms(Space space) {
        return spaceFormRepository.findByTitleAndSpaceId("Job Application", space.getId())
                .orElseGet(() -> {
                    SpaceForm form = SpaceForm.builder()
                            .spaceId(space.getId())
                            .title("Job Application")
                            .description("Apply for backend engineer role")
                            .status("ACTIVE")
                            .createdAt(Instant.now())
                            .build();
                    return spaceFormRepository.save(form);
                });
    }

    private void seedFormFieldDefinitions(SpaceForm form, FieldDefinition fieldDef) throws JsonProcessingException {
        boolean exists = formFieldDefinitionRepository.existsByFormIdAndFieldDefinitionId(form.getId(), fieldDef.getId());
        if (!exists) {
            Map<String, Object> uiOverride = new HashMap<>();
            uiOverride.put("placeholder", "example@company.com");
            uiOverride.put("width", 12);
            uiOverride.put("icon", "email");

            Map<String, Object> validationOverride = new HashMap<>();
            validationOverride.put("required", true);
            validationOverride.put("regex", "^[A-Za-z0-9+_.-]+@(.+)$");
            validationOverride.put("minLength", 5);
            validationOverride.put("maxLength", 100);

            FormFieldDefinition formField = FormFieldDefinition.builder()
                    .id(UUID.randomUUID())
                    .formId(form.getId())
                    .fieldDefinitionId(fieldDef.getId())
                    .displayOrder(1)
                    .labelOverride("Email Address")
                    .descriptionOverride("Primary contact email")
                    .uiOverride(uiOverride)
                    .validationOverride(validationOverride)
                    .status("ACTIVE")
                    .createdAt(Instant.now())
                    .build();
            formFieldDefinitionRepository.save(formField);
        }
    }

}
