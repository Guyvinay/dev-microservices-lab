package com.dev.service;

import com.dev.dto.UserProfileDetailsDto;
import com.dev.entity.UserProfileModel;

public interface OAuth2UserProfileService {
    UserProfileDetailsDto processOAuthPostLogin(String provider, String providerId, UserProfileModel userProfileModel);
}
