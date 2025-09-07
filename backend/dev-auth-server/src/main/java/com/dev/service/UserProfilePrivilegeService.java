package com.dev.service;

import com.dev.dto.PrivilegeAssignmentRequest;
import com.dev.dto.PrivilegeDTO;
import com.dev.entity.UserProfilePrivilegeModel;
import com.dev.entity.enums.Action;
import com.dev.entity.enums.Area;
import com.dev.entity.enums.Privilege;

import java.util.List;

public interface UserProfilePrivilegeService {

    UserProfilePrivilegeModel assignPrivilegeToRole(PrivilegeAssignmentRequest request);

    void revokePrivilegeFromRole(PrivilegeAssignmentRequest request);

    List<UserProfilePrivilegeModel> getPrivilegesByRole(Long roleId);
    List<PrivilegeDTO> getPrivilegeCatalog(Long roleId);
}
