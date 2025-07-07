package com.dev.security.provider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // Send a 401 Unauthorized response instead of redirecting
        response.sendRedirect("/saml2/authenticate/okta"); // <-- triggers login flow

//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized - Please log in");
    }
}
