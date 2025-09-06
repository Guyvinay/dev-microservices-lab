package com.dev.service;


import com.dev.dto.CreateRoleRequest;
import com.dev.dto.RoleDTO;

import java.util.List;

public interface UserProfileRoleService {
    RoleDTO createRole(CreateRoleRequest request);
    RoleDTO updateRole(Long roleId, RoleDTO request);
    RoleDTO getRoleById(Long roleId);
    List<RoleDTO> getRolesByTenant(String tenantId);
    void deleteRole(Long roleId);
}