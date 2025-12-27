package com.dev.service.impl;

import com.dev.entity.enums.Action;
import com.dev.entity.enums.Privilege;
import com.dev.repository.UserProfilePrivilegeRepository;
import com.dev.repository.UserProfileRoleModelRepository;
import com.dev.security.details.UserBaseInfo;
import com.dev.security.dto.AccessJwtToken;
import com.dev.service.AuthorizationEvaluator;
import com.dev.utility.grpc.RequiresRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationEvaluatorImpl implements AuthorizationEvaluator {

    private final UserProfileRoleModelRepository roleRepository;
    private final UserProfilePrivilegeRepository privilegeRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isAllowed(
            AccessJwtToken token,
            RequiresRequest request
    ) {

        UserBaseInfo user = token.getUserBaseInfo();

        List<Long> roleIds = safeParseRoleIds(user.getRoleIds());
        if (roleIds.isEmpty()) {
            return false;
        }

        List<Long> activeRoleIds =
                roleRepository.findActiveRoleIdsByTenantIdAndRoleIds(
                        user.getTenantId(),
                        roleIds
                );

        if (!activeRoleIds.isEmpty()) {
            return true;
        }

        if (activeRoleIds.isEmpty()) {
            return false;
        }

        // 3️⃣ No privilege requested → authenticated access allowed
        if (request.getPrivilege().isBlank()) {
            return true;
        }

        Privilege privilege = safePrivilege(request.getPrivilege());
        if (privilege == null) {
            return false;
        }

        request.getActionsList();

        // 4️⃣ Validate at least ONE action is permitted
        for (String actionStr : request.getActionsList()) {

            Action action = safeAction(actionStr);
            if (action == null) {
                continue;
            }

            boolean allowed =
                    privilegeRepository.existsByRoleIdsAndPrivilegeAndAction(
                            activeRoleIds,
                            privilege,
                            action
                    );

            if (allowed) {
                return true;
            }
        }

        return false;
    }

    // ---------- helpers ----------

    private List<Long> safeParseRoleIds(List<String> roles) {
        return roles.stream()
                .map(r -> {
                    try {
                        return Long.valueOf(r);
                    } catch (Exception ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private Privilege safePrivilege(String privilege) {
        try {
            return Privilege.valueOf(privilege);
        } catch (Exception ex) {
            log.warn("Invalid privilege: {}", privilege);
            return null;
        }
    }

    private Action safeAction(String action) {
        try {
            return Action.valueOf(action);
        } catch (Exception ex) {
            log.warn("Invalid action: {}", action);
            return null;
        }
    }
}

