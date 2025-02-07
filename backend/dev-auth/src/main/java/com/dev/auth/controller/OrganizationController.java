package com.dev.auth.controller;

import com.dev.auth.dto.OrganizationDTO;
import com.dev.auth.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1.0/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<OrganizationDTO> createOrganization(@Valid @RequestBody OrganizationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(organizationService.createOrganization(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable UUID id) {
        return ResponseEntity.ok(organizationService.getOrganizationById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrganizationDTO>> getAllOrganizations() {
        return ResponseEntity.ok(organizationService.getAllOrganizations());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationDTO> updateOrganization(@PathVariable UUID id,
                                                              @Valid @RequestBody OrganizationDTO dto) {
        return ResponseEntity.ok(organizationService.updateOrganization(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable UUID id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }

}
