package com.dev.library.oauth2.dto;

import java.util.Map;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return getAttributeByKey("sub");
    }

    @Override
    public String getName() {
        return getAttributeByKey("name");
    }

    @Override
    public String getEmail() {
        return getAttributeByKey("email");
    }

    @Override
    public String getAttributeByKey(String key) {
        return attributes != null && !attributes.isEmpty() ? (String) attributes.get(key) : null;
    }

}
