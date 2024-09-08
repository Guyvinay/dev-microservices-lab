package com.dev.profile.controller;

import com.dev.profile.entity.UserRole;
import com.dev.profile.service.UserRoleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1.0/role")
public class UserRoleController {
    @Autowired
    private UserRoleService userRoleService;

    // Create a new UserRole
    @PostMapping
    public ResponseEntity<UserRole> createUserRole(@RequestBody UserRole userRole) {
        UserRole createdRole = userRoleService.createUserRole(userRole);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }

    // Get a UserRole by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserRole> getUserRoleById(@PathVariable UUID id) {
        UserRole userRole = userRoleService.getUserRoleById(id);
        return new ResponseEntity<>(userRole, HttpStatus.OK);
    }

    // Update an existing UserRole
    @PutMapping("/{id}")
    public ResponseEntity<UserRole> updateUserRole(@PathVariable UUID id, @RequestBody UserRole userRoleDetails) {
        UserRole updatedRole = userRoleService.updateUserRole(id, userRoleDetails);
        return new ResponseEntity<>(updatedRole, HttpStatus.OK);
    }

    // Get all UserRoles
    @GetMapping
    public ResponseEntity<List<UserRole>> getAllUserRoles() {
        List<UserRole> roles = userRoleService.getAllUserRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    // Delete a UserRole by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserRole(@PathVariable UUID id) {
        userRoleService.deleteUserRole(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
