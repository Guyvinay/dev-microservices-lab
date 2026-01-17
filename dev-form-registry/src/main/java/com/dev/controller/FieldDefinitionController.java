package com.dev.controller;

import com.dev.dto.FieldDefinitionDto;
import com.dev.service.FieldDefinitionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fields")
public class FieldDefinitionController {

    private final FieldDefinitionService service;

    public FieldDefinitionController(FieldDefinitionService service) {
        this.service = service;
    }

    @PostMapping
    public FieldDefinitionDto create(@RequestBody FieldDefinitionDto dto) {
        return service.create(dto);
    }

    @GetMapping("/space/{spaceId}")
    public List<FieldDefinitionDto> getBySpace(@PathVariable UUID spaceId) {
        return service.getBySpace(spaceId);
    }
}
