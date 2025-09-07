package com.dev.service;

import com.dev.entity.UserProfilePrivilegeModel;
import com.dev.entity.enums.Action;
import com.dev.entity.enums.Area;
import com.dev.entity.enums.Privilege;

import java.util.List;

public interface UserProfilePrivilegeService {

    UserProfilePrivilegeModel assignPrivilegeToRole(Long roleId, Privilege privilege, Action action, Area area, Long assignedBy);

    void revokePrivilegeFromRole(Long roleId, Privilege privilege, Action action, Area area);

    List<UserProfilePrivilegeModel> getPrivilegesByRole(Long roleId);
}
