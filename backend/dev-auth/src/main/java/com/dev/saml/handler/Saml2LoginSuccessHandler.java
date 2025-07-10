package com.dev.saml.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

//@Component
@Slf4j
public class Saml2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        Saml2Authentication saml2Auth = (Saml2Authentication) authentication;
        DefaultSaml2AuthenticatedPrincipal principal =
                (DefaultSaml2AuthenticatedPrincipal) saml2Auth.getPrincipal();
        String username = principal.getName();

        log.info("SAML Login success for user: {}", username);

        // Optional: Create a structured JSON response instead
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        String json = new ObjectMapper().writeValueAsString(body);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
        response.getWriter().flush();

        // Important: Ensure response is completed so nothing else attempts to write
        response.flushBuffer();
    }
}