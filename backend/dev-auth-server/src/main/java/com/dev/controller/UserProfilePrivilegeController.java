package com.dev.controller;

import com.dev.entity.UserProfilePrivilegeModel;
import com.dev.entity.enums.Action;
import com.dev.entity.enums.Area;
import com.dev.entity.enums.Privilege;
import com.dev.service.UserProfilePrivilegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles/{roleId}/privileges")
@RequiredArgsConstructor
public class UserProfilePrivilegeController {

    private final UserProfilePrivilegeService privilegeService;

    @PostMapping
    public ResponseEntity<UserProfilePrivilegeModel> assignPrivilege(
            @PathVariable Long roleId,
            @RequestParam Privilege privilege,
            @RequestParam Action action,
            @RequestParam Area area,
            @RequestParam Long assignedBy
    ) {
        UserProfilePrivilegeModel assigned = privilegeService.assignPrivilegeToRole(roleId, privilege, action, area, assignedBy);
        return ResponseEntity.ok(assigned);
    }

    @DeleteMapping
    public ResponseEntity<Void> revokePrivilege(
            @PathVariable Long roleId,
            @RequestParam Privilege privilege,
            @RequestParam Action action,
            @RequestParam Area area
    ) {
        privilegeService.revokePrivilegeFromRole(roleId, privilege, action, area);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserProfilePrivilegeModel>> getPrivileges(
            @PathVariable Long roleId
    ) {
        return ResponseEntity.ok(privilegeService.getPrivilegesByRole(roleId));
    }
}
