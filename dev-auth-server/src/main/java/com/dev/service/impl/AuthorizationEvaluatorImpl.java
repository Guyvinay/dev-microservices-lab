package com.dev.service.impl;

import com.dev.dto.privilege.Action;
import com.dev.dto.privilege.Privilege;
import com.dev.entity.UserProfileRoleModel;
import com.dev.repository.UserProfilePrivilegeRepository;
import com.dev.repository.UserProfileRoleModelRepository;
import com.dev.security.details.UserBaseInfo;
import com.dev.service.AuthorizationEvaluator;
import com.dev.utility.AuthContextUtil;
import com.dev.utility.grpc.MatchMode;
import com.dev.utility.grpc.PrivilegeActions;
import com.dev.utility.grpc.RequiresRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationEvaluatorImpl implements AuthorizationEvaluator {

    private final UserProfileRoleModelRepository roleRepository;
    private final UserProfilePrivilegeRepository privilegeRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isAllowed(RequiresRequest request) {

        UserBaseInfo user = AuthContextUtil.getUserBaseInfo();

        List<Long> roleIds = user.getRoleIds().stream().map(Long::valueOf).toList();

        log.info("Evaluating authorization | userId={} | tenant={} | match={} roles={}", user.getId(), user.getTenantId(), request.getMatchMode(), roleIds);

        List<UserProfileRoleModel> activeRoles = roleRepository.findByRoleIdInAndTenantIdAndIsActiveTrue(roleIds, user.getTenantId());

        if (activeRoles.isEmpty()) {
            log.warn("Authorization denied: No active roles");
            return false;
        }

        List<Long> activeRoleIds = activeRoles.stream().map(UserProfileRoleModel::getRoleId).toList();

        // ---------- Admin short-circuit ----------
        if (activeRoles.stream().anyMatch(UserProfileRoleModel::isAdminFlag)) {
            log.info("Authorization granted: Admin role | roles={}", activeRoleIds);
            return true;
        }

        Map<Privilege, Set<Action>> required = extractRequiredPrivileges(request);

        boolean isAll = request.getMatchMode() == MatchMode.ALL;

        // ---------- Privilege evaluation ----------
        for (Map.Entry<Privilege, Set<Action>> entry : required.entrySet()) {

            boolean hasPrivilege = privilegeRepository.existsByRoleIdsAndPrivilegeAndActions(
                    activeRoleIds, entry.getKey(), entry.getValue()
            );

            if (isAll && !hasPrivilege) {
                log.warn("Authorization denied (ALL) | missing privilege={} | actions={}", entry.getKey(), entry.getValue());
                return false;
            }

            if (!isAll && hasPrivilege) {
                log.info("Authorization granted (ANY) | privilege={} | actions={}", entry.getKey(), entry.getValue());
                return true;
            }
        }

        log.info("Authorization response={}", isAll);
        return isAll;
    }


    /**
     * Extracts and validates privilege-action pairs from gRPC request
     */
    private Map<Privilege, Set<Action>> extractRequiredPrivileges(RequiresRequest request) {

        Map<Privilege, Set<Action>> result = new EnumMap<>(Privilege.class);

        for (PrivilegeActions pa : request.getRequiredList()) {

            Privilege privilege = Privilege.valueOf(pa.getPrivilege());

            Set<Action> actions = EnumSet.noneOf(Action.class);

            for (String actionStr : pa.getActionsList()) {
                actions.add(Action.valueOf(actionStr));
            }
            result.put(privilege, actions);
        }
        return result;
    }
}

