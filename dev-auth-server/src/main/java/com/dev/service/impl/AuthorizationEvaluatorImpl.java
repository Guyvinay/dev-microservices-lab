package com.dev.service.impl;

import com.dev.dto.privilege.Action;
import com.dev.dto.privilege.Privilege;
import com.dev.repository.UserProfilePrivilegeRepository;
import com.dev.repository.UserProfileRoleModelRepository;
import com.dev.security.details.UserBaseInfo;
import com.dev.security.dto.AccessJwtToken;
import com.dev.service.AuthorizationEvaluator;
import com.dev.utility.grpc.PrivilegeActions;
import com.dev.utility.grpc.RequiresRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.EnumSet;
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
    public boolean isAllowed(
            AccessJwtToken token,
            RequiresRequest request
    ) {
        Map<Privilege, Set<Action>> required = extractRequiredPrivileges(request);
        log.info("Privilege request in AuthorizationEvaluator: {}", required);

        return true;
    }


    /**
     * Extracts and validates privilege-action pairs from gRPC request
     */
    private Map<Privilege, Set<Action>> extractRequiredPrivileges(RequiresRequest request) {

        Map<Privilege, Set<Action>> result = new EnumMap<>(Privilege.class);

        for (PrivilegeActions pa : request.getRequiredList()) {

            Privilege privilege;
            try {
                privilege = Privilege.valueOf(pa.getPrivilege());
            } catch (IllegalArgumentException ex) {
                log.warn("Unknown privilege received: {}", pa.getPrivilege());
                continue;
            }

            Set<Action> actions = EnumSet.noneOf(Action.class);

            for (String actionStr : pa.getActionsList()) {
                try {
                    actions.add(Action.valueOf(actionStr));
                } catch (IllegalArgumentException ex) {
                    log.warn(
                            "Unknown action '{}' for privilege '{}'",
                            actionStr, privilege
                    );
                }
            }

            if (!actions.isEmpty()) {
                result.computeIfAbsent(privilege, k -> EnumSet.noneOf(Action.class))
                        .addAll(actions);
            }
        }
        return result;
    }
}

