package com.dev.auth.oauth2.service;

import com.dev.auth.dto.JwtTokenDto;
import com.dev.auth.entity.UserProfileModel;
import com.dev.auth.oauth2.dto.CustomOAuth2User;
import com.dev.auth.security.utility.SecurityUtils;
import com.dev.auth.service.OAuth2UserProfileService;
import com.dev.auth.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserProfileService userProfileService;
    private final OAuth2UserProfileService oAuth2UserProfileService;
    private final SecurityUtils securityUtils;


    public CustomOAuth2UserService(UserProfileService userProfileService, OAuth2UserProfileService oAuth2UserProfileService, SecurityUtils securityUtils) {
        this.userProfileService = userProfileService;
        this.oAuth2UserProfileService = oAuth2UserProfileService;
        this.securityUtils = securityUtils;
    }

    /**
     * @param userRequest 
     * @return
     * @throws OAuth2AuthenticationException
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        // Extract user details
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oauth2User.getAttribute("id").toString();
        String name = oauth2User.getAttribute("name");
        String email = oauth2User.getAttribute("email");

        log.info("provider: {}, providerId: {}, name: {}, email: {}", provider, providerId, name, email);

        UserProfileModel userProfileModel = UserProfileModel.builder()
                .name(name)
                .email(email)
                .isActive(true)
                .createdAt(Instant.now().toEpochMilli())
                .updatedAt(Instant.now().toEpochMilli())
                .build();

        UserProfileModel profileModel = oAuth2UserProfileService.processOAuthPostLogin(provider, providerId,  userProfileModel);
        log.info("User profile processed: {}", profileModel.getId());

        JwtTokenDto jwtTokenDto = securityUtils.createJwtTokeDtoFromModel(profileModel, 2);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(jwtTokenDto, oauth2User.getAttributes(), oauth2User.getAuthorities());

        return customOAuth2User;
    }
}
