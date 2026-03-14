package com.dev.dto;

import com.dev.entity.FieldDefinition;
import com.dev.entity.FormFieldDefinition;

public record FormFieldWithDefinition(
        FormFieldDefinition formField,
        FieldDefinition definition
) {}
