package com.dev.auth.service;

import com.dev.auth.entity.UserProfileModel;

public interface OAuth2UserProfileService {
    public UserProfileModel processOAuthPostLogin(String provider, String providerId, UserProfileModel userProfileModel);
}
