package com.dev.service;

import com.dev.entity.UserProfileModel;

public interface OAuth2UserProfileService {
    public UserProfileModel processOAuthPostLogin(String provider, String providerId, UserProfileModel userProfileModel);
}
