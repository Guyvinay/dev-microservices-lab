package com.dev.service;

import com.dev.dto.FormFieldDto;

import java.util.List;
import java.util.UUID;

public interface FormFieldService {
    FormFieldDto create(FormFieldDto dto);
    List<FormFieldDto> findByFormId(UUID formId);
}
