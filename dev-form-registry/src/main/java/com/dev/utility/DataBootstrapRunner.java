package com.dev.utility;

import com.dev.dto.FieldDefinitionDto;
import com.dev.dto.FormDto;
import com.dev.dto.FormFieldDto;
import com.dev.dto.SpaceDto;
import com.dev.service.FieldDefinitionService;
import com.dev.service.FormFieldService;
import com.dev.service.FormService;
import com.dev.service.SpaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class DataBootstrapRunner implements ApplicationRunner {

    private final SpaceService spaceService;
    private final FieldDefinitionService fieldService;
    private final FormService formService;
    private final FormFieldService formFieldService;

    public DataBootstrapRunner(
            SpaceService spaceService,
            FieldDefinitionService fieldService,
            FormService formService,
            FormFieldService formFieldService
    ) {
        this.spaceService = spaceService;
        this.fieldService = fieldService;
        this.formService = formService;
        this.formFieldService = formFieldService;
    }

    @Override
    public void run(ApplicationArguments args) {

        String tenantId = "public";

        // ---------- CHECK IF DATA EXISTS ----------

        List<SpaceDto> spaces = spaceService.getByTenant(tenantId);

        if (!spaces.isEmpty()) {
            log.info("Bootstrap skipped: data already exists");
            return;
        }

        log.info("Starting Dynamic Forms Bootstrap...");

        // ---------- CREATE SPACE ----------

        SpaceDto space = new SpaceDto();

        space.setTenantId(tenantId);
        space.setName("Survey");
        space.setDescription("Space for Survey dynamic forms");
        space.setCreatedBy("SYSTEM");

        SpaceDto savedSpace = spaceService.create(space);

        UUID spaceId = savedSpace.getId();

        // ---------- CREATE FIELD DEFINITIONS ----------

        FieldDefinitionDto nameField = new FieldDefinitionDto();

        nameField.setSpaceId(spaceId);
        nameField.setType("TEXT");
        nameField.setLabel("Full Name");
        nameField.setDataType("STRING");
        nameField.setUiJson("""
                {
                  "placeholder":"Enter full name",
                  "component":"input"
                }
                """);
        nameField.setValidationJson("""
                {
                  "required":true,
                  "minLength":3
                }
                """);
        nameField.setCreatedBy("SYSTEM");

        FieldDefinitionDto savedNameField =
                fieldService.create(nameField);

        FieldDefinitionDto emailField = new FieldDefinitionDto();

        emailField.setSpaceId(spaceId);
        emailField.setType("TEXT");
        emailField.setLabel("Email Address");
        emailField.setDataType("STRING");
        emailField.setUiJson("""
                {
                  "placeholder":"Enter email",
                  "component":"input",
                  "subType":"email"
                }
                """);
        emailField.setValidationJson("""
                {
                  "required":true,
                  "regex":"^[A-Za-z0-9+_.-]+@(.+)$"
                }
                """);
        emailField.setCreatedBy("SYSTEM");

        FieldDefinitionDto savedEmailField =
                fieldService.create(emailField);

        // ---------- CREATE FORM ----------

        FormDto jobForm = new FormDto();

        jobForm.setSpaceId(spaceId);
        jobForm.setName("Job Application");
        jobForm.setDescription("Backend developer application form");
        jobForm.setCreatedBy("SYSTEM");

        FormDto savedForm = formService.create(jobForm);

        UUID formId = savedForm.getId();

        // ---------- MAP FIELDS TO FORM ----------

        FormFieldDto nameMapping = new FormFieldDto();

        nameMapping.setFormId(formId);
        nameMapping.setFieldId(savedNameField.getId());
        nameMapping.setFieldOrder(1);
        nameMapping.setRequiredOverride(true);

        formFieldService.create(nameMapping);

        FormFieldDto emailMapping = new FormFieldDto();

        emailMapping.setFormId(formId);
        emailMapping.setFieldId(savedEmailField.getId());
        emailMapping.setFieldOrder(2);
        emailMapping.setRequiredOverride(true);

        emailMapping.setOverrideJson("""
                {
                  "label":"Official Email",
                  "ui":{
                    "placeholder":"Enter company email"
                  }
                }
                """);

        formFieldService.create(emailMapping);

        log.info("Dynamic Forms Bootstrap Completed Successfully");
    }
}
