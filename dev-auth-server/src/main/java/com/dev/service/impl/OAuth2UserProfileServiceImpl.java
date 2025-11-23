package com.dev.service.impl;

import com.dev.entity.OrganizationTenantMapping;
import com.dev.entity.UserProfileModel;
import com.dev.entity.UserProfileTenantMapping;
import com.dev.oauth2.dto.OAuthProvider;
import com.dev.repository.OAuthProviderRepository;
import com.dev.repository.OrganizationTenantMappingRepository;
import com.dev.repository.UserProfileModelRepository;
import com.dev.repository.UserProfileTenantMappingRepository;
import com.dev.service.OAuth2UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2UserProfileServiceImpl implements OAuth2UserProfileService {

    private final UserProfileModelRepository userProfileModelRepository;
    private final OAuthProviderRepository oAuthProviderRepository;
    private final UserProfileTenantMappingRepository userProfileTenantMappingRepository;
    private final OrganizationTenantMappingRepository tenantMappingRepository;

    /**
     * @param provider 
     * @param providerId
     * @param userProfileModel
     * @return
     */
    @Override
    @Transactional
    public UserProfileModel processOAuthPostLogin(String provider, String providerId, UserProfileModel userProfileModel) {
        Optional<UserProfileModel> userProfile =  userProfileModelRepository.findByEmail(userProfileModel.getEmail());
        if (userProfile.isPresent()) {
            UserProfileModel  profileModel = userProfile.get();
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
            return profileModel;
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
        return savedUserProfileModel;
    }
}
