package com.dev.auth.controller;

import com.dev.auth.dto.OrganizationTenantDTO;
import com.dev.auth.service.OrganizationTenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tenants")
@RequiredArgsConstructor
public class OrganizationTenantController {

    private final OrganizationTenantService tenantService;

    @PostMapping
    public ResponseEntity<OrganizationTenantDTO> createTenant(@Valid @RequestBody OrganizationTenantDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantService.createTenant(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationTenantDTO> getTenantById(@PathVariable String id) {
        return ResponseEntity.ok(tenantService.getTenantById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrganizationTenantDTO>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationTenantDTO> updateTenant(@PathVariable String id,
                                                              @Valid @RequestBody OrganizationTenantDTO dto) {
        return ResponseEntity.ok(tenantService.updateTenant(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable String id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }
}
