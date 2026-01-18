package com.dev.service;

import com.dev.dto.FormDto;

import java.util.UUID;

public interface FormService {
    FormDto create(FormDto dto);
    FormDto getById(UUID uuid);

    void updateStatus(UUID formId, String published);
}
