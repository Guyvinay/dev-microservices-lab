package com.dev.service.impl;
import com.dev.entity.UserProfilePrivilegeModel;
import com.dev.entity.enums.Action;
import com.dev.entity.enums.Area;
import com.dev.entity.enums.Privilege;
import com.dev.repository.UserProfilePrivilegeRepository;
import com.dev.service.UserProfilePrivilegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfilePrivilegeServiceImpl implements UserProfilePrivilegeService {

    private final UserProfilePrivilegeRepository privilegeRepository;

    @Override
    public UserProfilePrivilegeModel assignPrivilegeToRole(Long roleId, Privilege privilege, Action action, Area area, Long assignedBy) {

        // Prevent duplicate assignment
        privilegeRepository.findByRoleIdAndPrivilegeAndActionAndArea(roleId, privilege, action, area)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Privilege already assigned to role.");
                });

        UserProfilePrivilegeModel model = new UserProfilePrivilegeModel();
        model.setRoleId(roleId);
        model.setPrivilege(privilege);
        model.setAction(action);
        model.setArea(area);
        model.setAssignedAt(Instant.now().toEpochMilli());
        model.setAssignedBy(assignedBy);

        return privilegeRepository.save(model);
    }

    @Override
    public void revokePrivilegeFromRole(Long roleId, Privilege privilege, Action action, Area area) {
        privilegeRepository.deleteByRoleIdAndPrivilegeAndActionAndArea(roleId, privilege, action, area);
    }

    @Override
    public List<UserProfilePrivilegeModel> getPrivilegesByRole(Long roleId) {
        return privilegeRepository.findByRoleId(roleId);
    }
}
