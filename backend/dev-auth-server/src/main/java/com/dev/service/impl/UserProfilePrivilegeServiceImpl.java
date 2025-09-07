package com.dev.service.impl;
import com.dev.dto.ActionDTO;
import com.dev.dto.PrivilegeAssignmentRequest;
import com.dev.dto.PrivilegeDTO;
import com.dev.entity.UserProfilePrivilegeModel;
import com.dev.entity.enums.Action;
import com.dev.entity.enums.Area;
import com.dev.entity.enums.Privilege;
import com.dev.repository.UserProfilePrivilegeRepository;
import com.dev.service.UserProfilePrivilegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfilePrivilegeServiceImpl implements UserProfilePrivilegeService {

    private final UserProfilePrivilegeRepository privilegeRepository;

    @Override
    public UserProfilePrivilegeModel assignPrivilegeToRole(PrivilegeAssignmentRequest request) {

        // Prevent duplicate assignment
        privilegeRepository.findByRoleIdAndPrivilegeAndActionAndArea(request.getRoleId(), request.getPrivilege(), request.getAction(), request.getArea())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Privilege already assigned to role.");
                });

        UserProfilePrivilegeModel model = new UserProfilePrivilegeModel();
        model.setRoleId(request.getRoleId());
        model.setPrivilege(request.getPrivilege());
        model.setAction(request.getAction());
        model.setArea(request.getArea());
        model.setAssignedAt(Instant.now().toEpochMilli());
        model.setAssignedBy(request.getAssignedBy());

        return privilegeRepository.save(model);
    }

    @Override
    public void revokePrivilegeFromRole(PrivilegeAssignmentRequest request) {
        privilegeRepository.deleteByRoleIdAndPrivilegeAndActionAndArea(request.getRoleId(), request.getPrivilege(), request.getAction(), request.getArea());
    }

    @Override
    public List<UserProfilePrivilegeModel> getPrivilegesByRole(Long roleId) {
        return privilegeRepository.findByRoleId(roleId);
    }

    @Override
    public List<PrivilegeDTO> getPrivilegeCatalog(Long roleId) {
        return Arrays.stream(Privilege.values())
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
    }
}
