package com.dev.utility;

import com.dev.dto.FieldSeedDefinition;
import com.dev.entity.*;
import com.dev.repo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class FormRegistryDataSeeder implements ApplicationRunner {

    private final FieldTypeRepository fieldTypeRepository;
    private final SpaceRepository spaceRepository;
    private final FieldDefinitionRepository fieldDefinitionRepository;
    private final SpaceFormRepository spaceFormRepository;
    private final FormFieldDefinitionRepository formFieldDefinitionRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting default data seeding...");

        seedFieldTypes();
        Space hrSpace = seedSpaces();
        List<FieldDefinition> emailField = seedFieldDefinitions(hrSpace, columns);
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

    private List<FieldDefinition> seedFieldDefinitions(
            Space space,
            List<FieldSeedDefinition> fields
    ) {

        return fields.stream()
                .map(def -> fieldDefinitionRepository
                        .findByLabelAndSpaceId(def.getLabel(), space.getId())
                        .orElseGet(() -> {

                            FieldDefinition field = FieldDefinition.builder()
                                    .spaceId(space.getId())
                                    .fieldTypeId(def.getFieldTypeId())
                                    .label(def.getLabel())
                                    .description(def.getDescription())
                                    .ui(def.getUi())
                                    .validation(def.getValidation())
                                    .db(def.getDb())
                                    .createdAt(Instant.now())
                                    .build();

                            return fieldDefinitionRepository.save(field);
                        })).collect(Collectors.toList());
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
    private void seedFormFieldDefinitions(
            SpaceForm form,
            List<FieldDefinition> fields
    ) {

        for (FieldDefinition field : fields) {

            formFieldDefinitionRepository
                    .findByFormIdAndFieldDefinitionId(form.getId(), field.getId())
                    .orElseGet(() -> {

                        FormFieldDefinition mapping = FormFieldDefinition.builder()
                                .formId(form.getId())
                                .fieldDefinitionId(field.getId())
                                .createdAt(Instant.now())
                                .labelOverride(field.getLabel())
                                .descriptionOverride(field.getDescription())
                                .dbOverride(field.getDb())
                                .uiOverride(field.getUi())
                                .validationOverride(field.getValidation())
                                .status("DFRAT")
                                .build();

                        return formFieldDefinitionRepository.save(mapping);
                    });
        }
    }

    private List<FieldSeedDefinition> columns = List.of(

            FieldSeedDefinition.builder()
                    .label("First Name")
                    .fieldTypeId(FieldTypeEnum.TEXT)
                    .description("Candidate first name")
                    .displayOrder(1)
                    .ui(Map.of(
                            "component", "input",
                            "placeholder", "Enter first name",
                            "width", 6
                    ))
                    .validation(Map.of(
                            "required", true,
                            "minLength", 2,
                            "maxLength", 50
                    ))
                    .db(Map.of(
                            "nullable", false,
                            "indexed", true
                    ))
                    .build(),

            FieldSeedDefinition.builder()
                    .label("Last Name")
                    .fieldTypeId(FieldTypeEnum.TEXT)
                    .description("Candidate last name")
                    .displayOrder(1)
                    .ui(Map.of(
                            "component", "input",
                            "placeholder", "Enter last name",
                            "width", 6
                    ))
                    .validation(Map.of(
                            "required", true,
                            "minLength", 2,
                            "maxLength", 50
                    ))
                    .db(Map.of(
                            "nullable", false,
                            "indexed", true
                    ))
                    .build(),

            FieldSeedDefinition.builder()
                    .label("Email Address")
                    .fieldTypeId(FieldTypeEnum.EMAIL)
                    .description("Primary contact email")
                    .displayOrder(2)
                    .ui(Map.of(
                            "component", "input",
                            "placeholder", "example@company.com",
                            "icon", "email",
                            "width", 12
                    ))
                    .validation(Map.of(
                            "required", true,
                            "regex", "^[A-Za-z0-9+_.-]+@(.+)$"
                    ))
                    .db(Map.of(
                            "nullable", false,
                            "unique", true,
                            "indexed", true
                    ))
                    .build(),

            FieldSeedDefinition.builder()
                    .label("Phone Number")
                    .fieldTypeId(FieldTypeEnum.TEXT)
                    .description("Candidate contact number")
                    .displayOrder(3)
                    .ui(Map.of(
                            "component", "input",
                            "placeholder", "+91 XXXXX XXXXX",
                            "icon", "phone",
                            "width", 12
                    ))
                    .validation(Map.of(
                            "required", true,
                            "regex", "^[0-9+\\- ]{10,15}$"
                    ))
                    .db(Map.of(
                            "nullable", false,
                            "indexed", true
                    ))
                    .build()
    );
}
