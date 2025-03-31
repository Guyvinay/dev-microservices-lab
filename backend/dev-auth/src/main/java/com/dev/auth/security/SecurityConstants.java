package com.dev.auth.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecurityConstants {

    @Value("${dev.auth.sign_secret_key:vyubjnvf9876ftvbefudnv78ref6g7-ewrfdvbhj-ewrfdhbn2134wer==}")
    private String signingSecretKey;

    public static final String AUTHORIZATION = "Authorization";


    public static final String BASIC_AUTH = "Basic";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public static final String ORGANIZATION = "organization";

    public static final String JWT_TOKEN = "JWT_TOKEN";
    public static final String JWT_REFRESH_TOKEN = "JWT_REFRESH_TOKEN";


    public String getSigningSecretKey() {
        return signingSecretKey;
    }
}
