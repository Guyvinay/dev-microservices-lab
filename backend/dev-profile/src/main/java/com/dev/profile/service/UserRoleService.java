package com.dev.profile.service;

import com.dev.profile.entity.UserRole;

import java.util.List;
import java.util.UUID;

public interface UserRoleService {

    // Create a new UserRole
    public UserRole createUserRole(UserRole userRole);

    // Update an existing UserRole
    public UserRole updateUserRole(UUID id, UserRole userRoleDetails);

    // Get UserRole by ID
    public UserRole getUserRoleById(UUID id);

    // Get all UserRoles
    public List<UserRole> getAllUserRoles();

    // Delete a UserRole by ID
    public void deleteUserRole(UUID id);

}
