package com.dev.service.impl;

import com.dev.dto.UserProfileDetailsDto;
import com.dev.entity.OrganizationTenantMapping;
import com.dev.entity.UserProfileModel;
import com.dev.entity.UserProfileTenantMapping;
import com.dev.oauth2.dto.OAuthProvider;
import com.dev.repository.OAuthProviderRepository;
import com.dev.repository.OrganizationTenantMappingRepository;
import com.dev.repository.UserProfileModelRepository;
import com.dev.repository.UserProfileRoleMappingRepository;
import com.dev.repository.UserProfileTenantMappingRepository;
import com.dev.service.OAuth2UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2UserProfileServiceImpl implements OAuth2UserProfileService {

    private final UserProfileModelRepository userProfileModelRepository;
    private final OAuthProviderRepository oAuthProviderRepository;
    private final UserProfileTenantMappingRepository userProfileTenantMappingRepository;
    private final OrganizationTenantMappingRepository tenantMappingRepository;
    private final UserProfileRoleMappingRepository roleMappingRepository;

    /**
     * @param provider 
     * @param providerId
     * @param userProfileModel
     * @return
     */
    @Override
    @Transactional
    public UserProfileDetailsDto processOAuthPostLogin(String provider, String providerId, UserProfileModel userProfileModel) {
        UserProfileDetailsDto profileDetailsDto = null;
        Optional<UserProfileModel> userProfile =  userProfileModelRepository.findByEmail(userProfileModel.getEmail());
        if (userProfile.isPresent()) {
            UserProfileModel profileModel = userProfile.get();
            UserProfileTenantMapping userProfileTenantMapping = userProfileTenantMappingRepository.findByUserId(profileModel.getId()).getFirst();
            List<String> roleIds = roleMappingRepository.findByUserId(profileModel.getId()).stream().map((role)-> String.valueOf(role.getRoleId())).collect(Collectors.toList());
            String userId = String.valueOf(profileModel.getId());
            Optional<OAuthProvider> oAuthProviderOptional = oAuthProviderRepository.findByUserIdAndProviderId(userId, providerId);
            if(oAuthProviderOptional.isEmpty()) {
                OAuthProvider oAuthProvider = OAuthProvider.builder()
                        .userId(userId)
                        .provider(provider)
                        .providerId(providerId)
                        .build();
                OAuthProvider savedOAuth = oAuthProviderRepository.save(oAuthProvider);
                log.info("OAuth saved for user: {}, with id: {}, providerName: {}, provideId: {} ", userId, savedOAuth.getId(), provider, providerId);
            }
            profileDetailsDto = new UserProfileDetailsDto(profileModel);
            profileDetailsDto.setOrgId(userProfileTenantMapping.getOrganizationId());
            profileDetailsDto.setTenantId(userProfileTenantMapping.getTenantId());
            profileDetailsDto.setRoleIds(roleIds);
            return profileDetailsDto;
        }

        UserProfileModel savedUserProfileModel = userProfileModelRepository.save(userProfileModel);

        OrganizationTenantMapping tenantMapping = tenantMappingRepository.findByTenantName("public")
                .orElseThrow(()-> new RuntimeException("Public tenant not exists, please contact admin."));

        UserProfileTenantMapping userProfileTenantMapping = new UserProfileTenantMapping();
        userProfileTenantMapping.setUserId(savedUserProfileModel.getId());
        userProfileTenantMapping.setTenantId(tenantMapping.getTenantId());
        userProfileTenantMapping.setOrganizationId(tenantMapping.getOrgId());

        userProfileTenantMappingRepository.save(userProfileTenantMapping);

        OAuthProvider oAuthProvider = OAuthProvider.builder()
                .userId(String.valueOf(savedUserProfileModel.getId()))
                .provider(provider)
                .providerId(providerId)
                .build();

        OAuthProvider savedOAuth = oAuthProviderRepository.save(oAuthProvider);
        log.info("OAuth and user profile saved, user: {}, with id: {}, providerName: {}, providerId: {}", savedUserProfileModel.getId(), savedOAuth.getId(), providerId, provider);
        profileDetailsDto = new UserProfileDetailsDto(savedUserProfileModel);
        profileDetailsDto.setTenantId(userProfileTenantMapping.getTenantId());
        profileDetailsDto.setOrgId(userProfileTenantMapping.getOrganizationId());

        return profileDetailsDto;
    }
}
