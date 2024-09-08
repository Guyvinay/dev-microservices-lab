package com.dev.profile.service.serivceImpl;

import com.dev.profile.entity.UserRole;
import com.dev.profile.exception.ResourceNotFoundException;
import com.dev.profile.repository.UserRoleRepository;
import com.dev.profile.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    // Create a new UserRole
    @Override
    public UserRole createUserRole(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }

    // Update an existing UserRole
    @Override
    public UserRole updateUserRole(UUID id, UserRole userRoleDetails) {
        UserRole existingRole = userRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserRole not found with id: " + id));

        existingRole.setName(userRoleDetails.getName());
        existingRole.setAuthorities(userRoleDetails.getAuthorities());

        return userRoleRepository.save(existingRole);
    }

    // Get UserRole by ID
    @Override
    public UserRole getUserRoleById(UUID id) {
        return userRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserRole not found with id: " + id));
    }

    // Get all UserRoles
    @Override
    public List<UserRole> getAllUserRoles() {
        return userRoleRepository.findAll();
    }

    // Delete a UserRole by ID
    @Override
    public void deleteUserRole(UUID id) {
        UserRole existingRole = userRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserRole not found with id: " + id));
        userRoleRepository.delete(existingRole);
    }
}
