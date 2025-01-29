package com.dev.auth.security.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomBcryptEncoder extends BCryptPasswordEncoder {

    private static final Logger log = LoggerFactory.getLogger(CustomBcryptEncoder.class);

    public CustomBcryptEncoder() {
        super(12);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        boolean matched = false;
        try {
            matched = super.matches(rawPassword, encodedPassword);
        } catch (Exception ex) {
            log.warn("exception in matching passwords.");
        }
        return matched;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return super.encode(rawPassword);
    }

}
