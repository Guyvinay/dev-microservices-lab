package com.dev.service;

import com.dev.dto.privilege.Action;
import com.dev.dto.privilege.Area;
import com.dev.dto.privilege.Privilege;
import com.dev.entity.OrganizationModel;
import com.dev.entity.OrganizationTenantMapping;
import com.dev.entity.UserProfileModel;
import com.dev.entity.UserProfilePrivilegeModel;
import com.dev.entity.UserProfileRoleMapping;
import com.dev.entity.UserProfileRoleModel;
import com.dev.entity.UserProfileTenantMapping;
import com.dev.repository.OrganizationModelRepository;
import com.dev.repository.OrganizationTenantMappingRepository;
import com.dev.repository.UserProfileModelRepository;
import com.dev.repository.UserProfilePrivilegeRepository;
import com.dev.repository.UserProfileRoleMappingRepository;
import com.dev.repository.UserProfileRoleModelRepository;
import com.dev.repository.UserProfileTenantMappingRepository;
import com.dev.security.provider.CustomBcryptEncoder;
import com.dev.utility.BootstrapProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SystemBootstrapService {

    private final OrganizationModelRepository organizationRepo;
    private final OrganizationTenantMappingRepository tenantRepo;
    private final UserProfileRoleModelRepository roleRepo;
    private final UserProfilePrivilegeRepository privilegeRepo;
    private final UserProfileModelRepository userRepo;
    private final UserProfileRoleMappingRepository userRoleRepo;
    private final UserProfileTenantMappingRepository userTenantRepo;
    private final CustomBcryptEncoder passwordEncoder;
    private final BootstrapProperties props;

    @Transactional
    public void bootstrap() {
        OrganizationModel systemOrg = createSystemOrg();
        createPublicTenant(systemOrg);
        UserProfileRoleModel adminRole = createAdminRole();
        createAdminPrivileges(adminRole);
        UserProfileModel adminUser = createAdminUser();
        mapUserToRoleAndTenant(adminUser, adminRole, systemOrg);
    }

    private OrganizationModel createSystemOrg() {
        return organizationRepo.findByOrgName(props.getSystemOrg().getName())
                .orElseGet(() -> {
                    OrganizationModel org = new OrganizationModel();
                    org.setOrgName(props.getSystemOrg().getName());
                    org.setOrgEmail(props.getSystemOrg().getEmail());
                    org.setOrgContact(props.getSystemOrg().getContact());
                    org.setCreatedAt(System.currentTimeMillis());
                    org.setUpdatedAt(System.currentTimeMillis());
                    org.setCreatedBy("SYSTEM");
                    log.info("Creating SYSTEM organization");
                    return organizationRepo.save(org);
                });
    }

    private void createPublicTenant(OrganizationModel org) {
        BootstrapProperties.PublicTenant publicTenant = props.getPublicTenant();
        tenantRepo.findById(publicTenant.getId()).orElseGet(() -> {
            OrganizationTenantMapping tenant = new OrganizationTenantMapping();
            tenant.setTenantId(publicTenant.getId());
            tenant.setTenantName(publicTenant.getName());
            tenant.setOrgId(org.getOrgId());
            tenant.setTenantActive(true);
            tenant.setCreatedAt(System.currentTimeMillis());
            tenant.setUpdatedAt(System.currentTimeMillis());
            log.info("Creating public tenant");
            return tenantRepo.save(tenant);
        });
    }

    private UserProfileRoleModel createAdminRole() {
        return roleRepo.findByRoleNameAndTenantId("ADMIN", "public")
                .orElseGet(() -> {
                    UserProfileRoleModel role = new UserProfileRoleModel();
                    role.setRoleId(10000L);
                    role.setRoleName("ADMIN");
                    role.setTenantId("public");
                    role.setAdminFlag(true);
                    role.setActive(true);
                    role.setDescription("System administrator role");
                    role.setCreatedAt(System.currentTimeMillis());
                    role.setUpdatedAt(System.currentTimeMillis());
                    log.info("Creating ADMIN role");
                    return roleRepo.save(role);
                });
    }

    private void createAdminPrivileges(UserProfileRoleModel adminRole) {
        if (privilegeRepo.existsByRoleId(adminRole.getRoleId())) {
            return;
        }

        List<UserProfilePrivilegeModel> privileges =
                Arrays.stream(Privilege.values())
                        .flatMap(p ->
                                Arrays.stream(Action.values())
                                        .map(a -> new UserProfilePrivilegeModel(
                                                null,
                                                adminRole.getRoleId(),
                                                p,
                                                a,
                                                Area.ADMIN_PANEL,
                                                System.currentTimeMillis(),
                                                null
                                        ))
                        )
                        .toList();

        privilegeRepo.saveAll(privileges);
        log.info("Admin privileges seeded: {}", privileges.size());
    }

    private UserProfileModel createAdminUser() {
        return userRepo.findByEmail(props.getAdmin().getEmail())
                .orElseGet(() -> {
                    UserProfileModel user = new UserProfileModel();
                    user.setEmail(props.getAdmin().getEmail());
                    user.setName(props.getAdmin().getName());
                    user.setPassword(passwordEncoder.encode(props.getAdmin().getPassword()));
                    user.setActive(true);
                    log.info("Creating admin user");
                    return userRepo.save(user);
                });
    }

    private void mapUserToRoleAndTenant(
            UserProfileModel user,
            UserProfileRoleModel role,
            OrganizationModel org
    ) {
        if (!userRoleRepo.existsByUserIdAndRoleId(user.getId(), role.getRoleId())) {
            userRoleRepo.save(new UserProfileRoleMapping(
                    null,
                    user.getId(),
                    role.getRoleId(),
                    true,
                    "public"
            ));
        }

        if (!userTenantRepo.existsByUserIdAndTenantId(user.getId(), "public")) {
            userTenantRepo.save(new UserProfileTenantMapping(
                    null,
                    "public",
                    user.getId(),
                    org.getOrgId()
            ));
        }
    }

}
