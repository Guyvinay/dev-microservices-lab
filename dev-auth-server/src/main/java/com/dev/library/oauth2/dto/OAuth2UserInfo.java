package com.dev.library.oauth2.dto;

public interface OAuth2UserInfo {
    String getProviderId();
    String getName();
    String getEmail();
    String getAttributeByKey(String key);
}
