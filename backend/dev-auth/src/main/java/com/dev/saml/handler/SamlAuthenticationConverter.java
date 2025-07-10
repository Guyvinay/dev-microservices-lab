package com.dev.saml.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

//@Component
public class SamlAuthenticationConverter implements AuthenticationConverter {

    @Override
    public AbstractAuthenticationToken convert(HttpServletRequest request) {
        return null;
    }
}
