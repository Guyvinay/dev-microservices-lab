package com.dev.service.impl;

import com.dev.dto.CreateRoleRequest;
import com.dev.security.dto.JwtTokenDto;
import com.dev.dto.RoleDTO;
import com.dev.entity.UserProfileRoleModel;
import com.dev.exception.ResourceNotFoundException;
import com.dev.repository.UserProfileRoleModelRepository;
import com.dev.service.UserProfileRoleService;
import com.dev.utility.AuthUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileRoleServiceImpl implements UserProfileRoleService {

    private final UserProfileRoleModelRepository roleRepository;

    @Override
    public RoleDTO createRole(CreateRoleRequest request) {
        if (roleRepository.existsByRoleNameAndTenantId(request.getRoleName(), request.getTenantId())) {
            throw new IllegalArgumentException("Role already exists for this tenant");
        }
        JwtTokenDto jwtTokenDto = (JwtTokenDto) SecurityContextHolder.getContext().getAuthentication().getDetails();
        UserProfileRoleModel role = new UserProfileRoleModel();
        long roleId = AuthUtility.generateRandomNumber(8);
        role.setRoleId(roleId);
        role.setRoleName(request.getRoleName());
        role.setTenantId(request.getTenantId());
        role.setAdminFlag(request.isAdminFlag());
        role.setDescription(request.getDescription());
        role.setActive(true);
        role.setCreatedAt(Instant.now().toEpochMilli());
        role.setUpdatedAt(Instant.now().toEpochMilli());
        role.setCreatedBy(jwtTokenDto.getUserBaseInfo().getEmail());
        role.setUpdatedBy(jwtTokenDto.getUserBaseInfo().getEmail());

        UserProfileRoleModel saved = roleRepository.save(role);
        return mapToDTO(saved);
    }

    @Override
    public RoleDTO updateRole(Long roleId, RoleDTO request) {
        UserProfileRoleModel role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        role.setRoleName(request.getRoleName());
        role.setAdminFlag(request.isAdminFlag());
        role.setDescription(request.getDescription());
        role.setActive(request.isActive());
        role.setUpdatedAt(Instant.now().toEpochMilli());
        role.setUpdatedBy(request.getUpdatedBy());

        UserProfileRoleModel updated = roleRepository.save(role);
        return mapToDTO(updated);
    }

    @Override
    public RoleDTO getRoleById(Long roleId) {
        return roleRepository.findById(roleId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }

    @Override
    public List<RoleDTO> getRolesByTenant(String tenantId) {
        return roleRepository.findByTenantId(tenantId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRole(Long roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role not found");
        }
        roleRepository.deleteById(roleId);
    }

    private RoleDTO mapToDTO(UserProfileRoleModel role) {
        return RoleDTO.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .active(role.isActive())
                .adminFlag(role.isAdminFlag())
                .tenantId(role.getTenantId())
                .description(role.getDescription())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .createdBy(role.getCreatedBy())
                .updatedBy(role.getUpdatedBy())
                .build();
    }
}
