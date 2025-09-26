package com.dev.controller;

import com.dev.dto.ActionDTO;
import com.dev.dto.PrivilegeAssignmentRequest;
import com.dev.dto.PrivilegeDTO;
import com.dev.entity.UserProfilePrivilegeModel;
import com.dev.entity.enums.Privilege;
import com.dev.service.UserProfilePrivilegeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/privileges")
@RequiredArgsConstructor
@Tag(name = "User Profile Privilege Management", description = "Endpoints for managing user profiles privileges")
public class UserProfilePrivilegeController {

    private final UserProfilePrivilegeService privilegeService;

    @GetMapping()
    public ResponseEntity<List<PrivilegeDTO>> getPrivilegeCatalog() {
        List<PrivilegeDTO> catalog = Arrays.stream(Privilege.values())
                .map(privilege -> {
                    List<ActionDTO> actions = privilege.getActions().stream()
                            .map(act -> new ActionDTO(act.name(), act.getDescription()))
                            .collect(Collectors.toList());

                    return new PrivilegeDTO(
                            privilege.name(),
                            privilege.getDescription(),
                            privilege.getArea().name(),
                            actions
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(catalog);
    }

    @PostMapping("/assign")
    public ResponseEntity<UserProfilePrivilegeModel> assignPrivilege(
            @RequestBody PrivilegeAssignmentRequest request
    ) {
        UserProfilePrivilegeModel assigned = privilegeService.assignPrivilegeToRole(request);
        return ResponseEntity.ok(assigned);
    }

    @DeleteMapping
    public ResponseEntity<Void> revokePrivilege(
            @RequestBody PrivilegeAssignmentRequest request
    ) {
        privilegeService.revokePrivilegeFromRole(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/role/{roleId}")
    public ResponseEntity<List<UserProfilePrivilegeModel>> getPrivileges(
            @PathVariable Long roleId
    ) {
        return ResponseEntity.ok(privilegeService.getPrivilegesByRole(roleId));
    }
}
