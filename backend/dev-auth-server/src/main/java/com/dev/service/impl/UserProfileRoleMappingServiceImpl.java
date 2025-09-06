package com.dev.service.impl;
import com.dev.dto.CreateRoleMappingRequest;
import com.dev.dto.RoleMappingDTO;
import com.dev.entity.UserProfileRoleMapping;
import com.dev.exception.ResourceNotFoundException;
import com.dev.repository.UserProfileRoleMappingRepository;
import com.dev.repository.UserProfileRoleModelRepository;
import com.dev.service.UserProfileRoleMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileRoleMappingServiceImpl implements UserProfileRoleMappingService {

    private final UserProfileRoleMappingRepository mappingRepository;
    private final UserProfileRoleModelRepository roleRepository;

    @Override
    public RoleMappingDTO assignRoleToUser(CreateRoleMappingRequest request) {
        // Validate role existence
        roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        // Prevent duplicate role assignment
        if (mappingRepository.existsByUserIdAndRoleId(request.getUserId(), request.getRoleId())) {
            throw new IllegalArgumentException("User already has this role");
        }
        UserProfileRoleMapping mapping = new UserProfileRoleMapping();
        mapping.setUserId(request.getUserId());
        mapping.setRoleId(request.getRoleId());
        mapping.setTenantId(request.getTenantId());
        mapping.setDefaultRole(request.getDefaultRole() != null && request.getDefaultRole());

        UserProfileRoleMapping saved = mappingRepository.save(mapping);
        return null;
    }


    @Override
    public List<RoleMappingDTO> getUserRoles(UUID userId) {
        return mappingRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleMappingDTO getDefaultRole(UUID userId, String tenantId) {
        return mappingRepository.findByUserIdAndTenantIdAndDefaultRoleTrue(userId, tenantId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found for user"));
    }

    @Override
    public void removeUserRole(UUID userId, Long roleId) {
        UserProfileRoleMapping mapping = mappingRepository.findByUserIdAndRoleId(userId, roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role mapping not found for user"));

        mappingRepository.delete(mapping);
    }

    private RoleMappingDTO mapToDTO(UserProfileRoleMapping mapping) {
        return RoleMappingDTO.builder()
                .id(mapping.getId())
                .userId(mapping.getUserId())
                .roleId(mapping.getRoleId())
                .defaultRole(mapping.getDefaultRole())
                .tenantId(mapping.getTenantId())
                .build();
    }
}
