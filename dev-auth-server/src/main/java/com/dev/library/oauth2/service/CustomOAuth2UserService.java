package com.dev.library.oauth2.service;

import com.dev.security.dto.AccessJwtToken;
import com.dev.dto.UserProfileDetailsDto;
import com.dev.entity.UserProfileModel;
import com.dev.library.oauth2.dto.CustomOAuth2User;
import com.dev.library.oauth2.dto.OAuth2UserInfo;
import com.dev.library.oauth2.factory.OAuth2UserInfoFactory;
import com.dev.security.provider.JwtTokenProviderManager;
import com.dev.service.OAuth2UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuth2UserProfileService oAuth2UserProfileService;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate =
            new DefaultOAuth2UserService();
    private final OAuth2UserInfoFactory userInfoFactory;
    private final JwtTokenProviderManager jwtTokenProviderManager;    @Value("${security.jwt.access-expiry-minutes}")
    private int accessExpiryMinutes;

    /**
     * @param userRequest 
     * @return
     * @throws OAuth2AuthenticationException
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        // Extract user details
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oauth2User.getAttributes();

        OAuth2UserInfo userInfo =
                userInfoFactory.getUserInfo(provider, attributes);

        validateOAuth2User(userInfo, provider);
        UserProfileModel profileModel = buildUserProfile(userInfo);

        UserProfileDetailsDto profileDetails =
                oAuth2UserProfileService.processOAuthPostLogin(
                        provider,
                        userInfo.getProviderId(),
                        profileModel
                );

        log.info("attributes: {}", attributes.size());

        AccessJwtToken accessJwtToken = jwtTokenProviderManager.createJwtTokeDtoFromModel(profileDetails, accessExpiryMinutes);

        return new CustomOAuth2User(accessJwtToken, attributes, oauth2User.getAuthorities());
    }

    private UserProfileModel buildUserProfile(OAuth2UserInfo userInfo) {

        long now = Instant.now().toEpochMilli();

        return UserProfileModel.builder()
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private void validateOAuth2User(OAuth2UserInfo userInfo, String provider) {

        if (!StringUtils.hasText(userInfo.getProviderId())) {
            throw new OAuth2AuthenticationException(
                    "Missing providerId from " + provider);
        }

        if (!StringUtils.hasText(userInfo.getEmail())) {
            throw new OAuth2AuthenticationException(
                    "Email not provided by " + provider);
        }
    }

}
