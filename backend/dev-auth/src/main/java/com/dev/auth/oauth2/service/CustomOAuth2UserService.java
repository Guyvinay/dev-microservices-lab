package com.dev.auth.oauth2.service;

import com.dev.auth.dto.UserProfileRequestDTO;
import com.dev.auth.dto.UserProfileResponseDTO;
import com.dev.auth.entity.UserProfileModel;
import com.dev.auth.repository.UserProfileModelRepository;
import com.dev.auth.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UserProfileService userProfileService;

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

        if(userProfileService.existsByEmail(email)) {
            UserProfileResponseDTO profileResponseDTO = userProfileService.getUserByEmail(email);
        } else {
            UserProfileRequestDTO userProfileModel = new UserProfileRequestDTO();
            userProfileModel.setIsActive(true);
            userProfileModel.setEmail(email);
            UserProfileResponseDTO savedUserProfileModel = userProfileService.createUser(userProfileModel);
        }

        return oauth2User;
    }
}
