package com.dev.service;


import com.dev.dto.CreateRoleMappingRequest;
import com.dev.dto.RoleMappingDTO;

import java.util.List;
import java.util.UUID;

public interface UserProfileRoleMappingService {
    RoleMappingDTO assignRoleToUser(CreateRoleMappingRequest request);
    List<RoleMappingDTO> getUserRoles(UUID userId);
    RoleMappingDTO getDefaultRole(UUID userId, String tenantId);
    void removeUserRole(UUID userId, Long roleId);
}