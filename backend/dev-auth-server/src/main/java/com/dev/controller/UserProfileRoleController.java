package com.dev.controller;

import com.dev.dto.CreateRoleRequest;
import com.dev.dto.RoleDTO;
import com.dev.service.UserProfileRoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "User Profile Role Management", description = "Endpoints for managing user profile roles")
public class UserProfileRoleController {

    private final UserProfileRoleService roleService;

    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@RequestBody CreateRoleRequest request) {
        return ResponseEntity.ok(roleService.createRole(request));
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<RoleDTO> updateRole(
            @PathVariable Long roleId,
            @RequestBody RoleDTO request) {
        return ResponseEntity.ok(roleService.updateRole(roleId, request));
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long roleId) {
        return ResponseEntity.ok(roleService.getRoleById(roleId));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<RoleDTO>> getRolesByTenant(@PathVariable String tenantId) {
        return ResponseEntity.ok(roleService.getRolesByTenant(tenantId));
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }
}
