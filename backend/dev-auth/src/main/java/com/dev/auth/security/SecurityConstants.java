package com.dev.auth.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecurityConstants {

    @Value("${dev.auth.sign_secret_key:vyubjnvf9876ftvbefudnv78ref6g7-ewrfdvbhj-ewrfdhbn2134wer==}")
    private String signingSecretKey;

    public static final String AUTHORIZATION = "Authorization";

    public String getSigningSecretKey() {
        return signingSecretKey;
    }
}
