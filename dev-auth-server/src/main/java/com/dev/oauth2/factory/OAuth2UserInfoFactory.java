package com.dev.oauth2.factory;

import com.dev.oauth2.dto.DefaultOAuth2UserInfo;
import com.dev.oauth2.dto.GithubOAuth2UserInfo;
import com.dev.oauth2.dto.GoogleOAuth2UserInfo;
import com.dev.oauth2.dto.OAuth2UserInfo;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OAuth2UserInfoFactory {

    public OAuth2UserInfo getUserInfo(String provider, Map<String, Object> attributes) {

        return switch (provider.toLowerCase()) {
            case "google" -> new GoogleOAuth2UserInfo(attributes);
            case "github" -> new GithubOAuth2UserInfo(attributes);
            default -> new DefaultOAuth2UserInfo(attributes);
        };
    }
}
