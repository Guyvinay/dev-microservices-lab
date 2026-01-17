package com.dev.controller;

import com.dev.dto.SpaceDto;
import com.dev.service.SpaceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/spaces")
public class SpaceController {

    private final SpaceService service;

    public SpaceController(SpaceService service) {
        this.service = service;
    }

    @PostMapping
    public SpaceDto create(@RequestBody SpaceDto dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public SpaceDto getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping("/tenant/{tenantId}")
    public List<SpaceDto> getByTenant(@PathVariable String tenantId) {
        return service.getByTenant(tenantId);
    }
}