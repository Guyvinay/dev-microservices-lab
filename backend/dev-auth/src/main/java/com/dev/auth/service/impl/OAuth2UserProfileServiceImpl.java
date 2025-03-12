package com.dev.auth.service.impl;

import com.dev.auth.entity.UserProfileModel;
import com.dev.auth.oauth2.dto.OAuthProvider;
import com.dev.auth.repository.OAuthProviderRepository;
import com.dev.auth.repository.UserProfileModelRepository;
import com.dev.auth.service.OAuth2UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class OAuth2UserProfileServiceImpl implements OAuth2UserProfileService {

    private final UserProfileModelRepository userProfileModelRepository;
    private final OAuthProviderRepository oAuthProviderRepository;

    public OAuth2UserProfileServiceImpl(UserProfileModelRepository userProfileModelRepository, OAuthProviderRepository oAuthProviderRepository) {
        this.userProfileModelRepository = userProfileModelRepository;
        this.oAuthProviderRepository = oAuthProviderRepository;
    }

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
