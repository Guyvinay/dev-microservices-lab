package com.dev.controller;

import com.dev.dto.CreateRoleMappingRequest;
import com.dev.dto.RoleMappingDTO;
import com.dev.service.UserProfileRoleMappingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/role-mappings")
@RequiredArgsConstructor
public class UserProfileRoleMappingController {

    private final UserProfileRoleMappingService mappingService;

    @PostMapping
    public ResponseEntity<RoleMappingDTO> assignRoleToUser(@RequestBody CreateRoleMappingRequest request) {
        return ResponseEntity.ok(mappingService.assignRoleToUser(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoleMappingDTO>> getUserRoles(@PathVariable UUID userId) {
        return ResponseEntity.ok(mappingService.getUserRoles(userId));
    }

    @GetMapping("/user/{userId}/tenant/{tenantId}/default")
    public ResponseEntity<RoleMappingDTO> getDefaultRole(
            @PathVariable UUID userId,
            @PathVariable String tenantId) {
        return ResponseEntity.ok(mappingService.getDefaultRole(userId, tenantId));
    }

    @DeleteMapping("/user/{userId}/role/{roleId}")
    public ResponseEntity<Void> removeUserRole(@PathVariable UUID userId, @PathVariable Long roleId) {
        mappingService.removeUserRole(userId, roleId);
        return ResponseEntity.noContent().build();
    }
}
