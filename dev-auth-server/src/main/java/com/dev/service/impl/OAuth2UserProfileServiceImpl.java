package com.dev.service.impl;

import com.dev.dto.UserProfileDetailsDto;
import com.dev.entity.OrganizationTenantMapping;
import com.dev.entity.UserProfileModel;
import com.dev.entity.UserProfileRoleMapping;
import com.dev.entity.UserProfileRoleModel;
import com.dev.entity.UserProfileTenantMapping;
import com.dev.library.oauth2.model.OAuthProvider;
import com.dev.repository.OAuthProviderRepository;
import com.dev.repository.OrganizationTenantMappingRepository;
import com.dev.repository.UserProfileModelRepository;
import com.dev.repository.UserProfileRoleMappingRepository;
import com.dev.repository.UserProfileRoleModelRepository;
import com.dev.repository.UserProfileTenantMappingRepository;
import com.dev.service.OAuth2UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.dev.utility.DefaultConstants.DEFAULT_ROLE;
import static com.dev.utility.DefaultConstants.PUBLIC_TENANT;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2UserProfileServiceImpl implements OAuth2UserProfileService {

    private final UserProfileModelRepository userProfileModelRepository;
    private final OAuthProviderRepository oAuthProviderRepository;
    private final UserProfileTenantMappingRepository userProfileTenantMappingRepository;
    private final OrganizationTenantMappingRepository tenantMappingRepository;
    private final UserProfileRoleMappingRepository roleMappingRepository;
    private final UserProfileRoleModelRepository roleModelRepository;


    /**
     * @param provider 
     * @param providerId
     * @param userProfileModel
     * @return
     */
    @Override
    @Transactional
    public UserProfileDetailsDto processOAuthPostLogin(String provider, String providerId, UserProfileModel userProfileModel) {

        UserProfileModel user = findOrCreateUser(userProfileModel);

        UserProfileTenantMapping tenantMapping = ensureTenantMapping(user);

        ensureDefaultRole(user, tenantMapping.getTenantId());

        ensureOAuthProviderLink(user, provider, providerId);

        return buildUserProfileDetails(user, tenantMapping);
    }

    private UserProfileModel findOrCreateUser(UserProfileModel incomingProfile) {
        return userProfileModelRepository.findByEmail(incomingProfile.getEmail())
                .orElseGet(() -> {
                    UserProfileModel saved = userProfileModelRepository.save(incomingProfile);
                    log.info("New user created with email={}", saved.getEmail());
                    return saved;
                });
    }

    private UserProfileTenantMapping ensureTenantMapping(UserProfileModel user) {

        return userProfileTenantMappingRepository.findByUserId(user.getId())
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    OrganizationTenantMapping publicTenant =
                            tenantMappingRepository.findById(PUBLIC_TENANT)
                                    .orElseThrow(() -> new IllegalStateException(
                                            "Public tenant does not exist"));

                    UserProfileTenantMapping mapping = new UserProfileTenantMapping();
                    mapping.setUserId(user.getId());
                    mapping.setTenantId(publicTenant.getTenantId());
                    mapping.setOrganizationId(publicTenant.getOrgId());

                    log.info("Tenant mapping created for userId={}", user.getId());
                    return userProfileTenantMappingRepository.save(mapping);
                });
    }
    private void ensureDefaultRole(UserProfileModel user, String tenantId) {

        boolean hasRoles = roleMappingRepository.existsByUserId(user.getId());
        if (hasRoles) {
            return;
        }

        UserProfileRoleModel role = roleModelRepository
                .findByRoleNameAndTenantId(DEFAULT_ROLE, tenantId)
                .orElseThrow(() -> new IllegalStateException(
                        "Default role USER not configured for tenant " + tenantId));

        UserProfileRoleMapping roleMapping = new UserProfileRoleMapping();
        roleMapping.setUserId(user.getId());
        roleMapping.setRoleId(role.getRoleId());
        roleMapping.setTenantId(tenantId);
        roleMapping.setDefaultRole(true);

        roleMappingRepository.save(roleMapping);
        log.info("Default USER role assigned to userId={}", user.getId());
    }

    private void ensureOAuthProviderLink(
            UserProfileModel user,
            String provider,
            String providerId) {

        Optional<OAuthProvider> existing =
                oAuthProviderRepository.findByUserIdAndProviderIdAndProvider(String.valueOf(user.getId()), providerId, provider);

        if (existing.isPresent()) {
            if (!existing.get().getUserId().equals(String.valueOf(user.getId()))) {
                throw new IllegalStateException(
                        "OAuth provider already linked to another user");
            }
            return;
        }

        OAuthProvider oauth = OAuthProvider.builder()
                .userId(String.valueOf(user.getId()))
                .provider(provider)
                .providerId(providerId)
                .build();

        try {
            oAuthProviderRepository.saveAndFlush(oauth);
            log.info("OAuth linked: userId={}, provider={}", user.getId(), provider);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException(
                    "OAuth provider already linked to another user", ex);
        }
    }

    private UserProfileDetailsDto buildUserProfileDetails(
            UserProfileModel user,
            UserProfileTenantMapping tenantMapping) {

        List<String> roleIds = roleMappingRepository.findByUserId(user.getId())
                .stream()
                .map(role -> String.valueOf(role.getRoleId()))
                .toList();

        UserProfileDetailsDto dto = new UserProfileDetailsDto(user);
        dto.setTenantId(tenantMapping.getTenantId());
        dto.setOrgId(tenantMapping.getOrganizationId());
        dto.setRoleIds(roleIds);

        return dto;
    }

}
