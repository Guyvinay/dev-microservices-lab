package com.dev.controller;

import com.dev.dto.UserProfileTenantDTO;
import com.dev.service.UserProfileTenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1.0/user-tenant-mappings")
@RequiredArgsConstructor
public class UserProfileTenantController {

    private final UserProfileTenantService service;

    @PostMapping
    public ResponseEntity<UserProfileTenantDTO> createMapping(@Valid @RequestBody UserProfileTenantDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createMapping(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileTenantDTO> getMappingById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getMappingById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserProfileTenantDTO>> getAllMappings() {
        return ResponseEntity.ok(service.getAllMappings());
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<UserProfileTenantDTO>> getMappingsByTenantId(@PathVariable String tenantId) {
        return ResponseEntity.ok(service.getMappingsByTenantId(tenantId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserProfileTenantDTO>> getMappingsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(service.getMappingsByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMapping(@PathVariable UUID id) {
        service.deleteMapping(id);
        return ResponseEntity.noContent().build();
    }
}
