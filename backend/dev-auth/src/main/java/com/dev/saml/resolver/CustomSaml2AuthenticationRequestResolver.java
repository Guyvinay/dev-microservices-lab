package com.dev.saml.resolver;

import com.dev.saml.repository.CustomRelyingPartyRegistrationRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.saml2.provider.service.authentication.AbstractSaml2AuthenticationRequest;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.authentication.OpenSaml4AuthenticationRequestResolver;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2AuthenticationRequestResolver;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class CustomSaml2AuthenticationRequestResolver implements Saml2AuthenticationRequestResolver {

    private final CustomRelyingPartyRegistrationRepository relyingPartyRegistrationRepository;

    public CustomSaml2AuthenticationRequestResolver(CustomRelyingPartyRegistrationRepository relyingPartyRegistrationRepository) {
        this.relyingPartyRegistrationRepository = relyingPartyRegistrationRepository;
    }

    @Override
    public <T extends AbstractSaml2AuthenticationRequest> T resolve(HttpServletRequest request) {
        String registrationId = extractRegistrationId(request);
        if (registrationId == null) {
            log.warn("Could not resolve registrationId from {}", request.getRequestURI());
            return null;
        }
        RelyingPartyRegistration registration = relyingPartyRegistrationRepository.findByRegistrationId(registrationId);

        if (registration == null) {
            log.warn("No RelyingPartyRegistration found for id {}", registrationId);
            return null;
        }




        return null;
    }

    @Bean
    public Saml2AuthenticationRequestResolver authenticationRequestResolver(RelyingPartyRegistrationRepository registrations) {
        RelyingPartyRegistrationResolver registrationResolver =
                new DefaultRelyingPartyRegistrationResolver(registrations);
        OpenSaml4AuthenticationRequestResolver authenticationRequestResolver =
                new OpenSaml4AuthenticationRequestResolver(registrationResolver);
        authenticationRequestResolver.setAuthnRequestCustomizer((context) -> context
                .getAuthnRequest().setForceAuthn(true));
        return authenticationRequestResolver;
    }

    private String extractRegistrationId(HttpServletRequest request) {
        String uri = request.getRequestURI(); // /login/saml2/sso/{registrationId}
        int idx = uri.lastIndexOf('/');
        return (idx > 0) ? uri.substring(idx + 1) : null;
    }
}
