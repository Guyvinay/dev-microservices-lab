package com.dev.oauth2.service;

import com.dev.dto.JwtTokenDto;
import com.dev.entity.UserProfileModel;
import com.dev.oauth2.dto.CustomOAuth2User;
import com.dev.security.utility.SecurityUtils;
import com.dev.service.OAuth2UserProfileService;
import com.dev.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

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
        Map<String, Object> attributes = oauth2User.getAttributes();
        log.info("attributes: {}", attributes.size());
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = String.valueOf(attributes.get("id"));
        if(provider.equalsIgnoreCase("google")) {
            providerId = String.valueOf(attributes.get("sub"));
        }
        String name = String.valueOf(attributes.get("name"));
        String email = String.valueOf(attributes.get("email"));

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

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(jwtTokenDto, attributes, oauth2User.getAuthorities());

        return customOAuth2User;
    }
}
