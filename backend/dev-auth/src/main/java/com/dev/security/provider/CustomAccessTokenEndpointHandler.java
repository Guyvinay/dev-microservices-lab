package com.dev.security.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomAccessTokenEndpointHandler implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private final DefaultAuthorizationCodeTokenResponseClient delegate = new DefaultAuthorizationCodeTokenResponseClient();

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        String registrationId = authorizationGrantRequest.getClientRegistration().getRegistrationId();
        String code = authorizationGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode();

        log.info("Exchanging code for token");
        log.info(" Code: {}, Provider: {}", code, registrationId);

        OAuth2AccessTokenResponse response = delegate.getTokenResponse(authorizationGrantRequest);

        OAuth2AccessToken oAuth2AccessToken = response.getAccessToken();

        log.info("Access Token: {}", oAuth2AccessToken.getTokenValue());
        log.info("Access Token Expires: {}", oAuth2AccessToken.getExpiresAt());

        return response;
    }
}
