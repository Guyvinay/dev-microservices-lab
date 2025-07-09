package com.dev.saml.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.Saml2MessageBinding;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CustomRelyingPartyRegistrationRepository implements RelyingPartyRegistrationRepository {

    private final Map<String, RelyingPartyRegistration> registrationMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        RelyingPartyRegistration oktaRegistration = relyingPartyRegistrationFromMetadata();
        registrationMap.put(oktaRegistration.getRegistrationId(), oktaRegistration);
        System.out.println("Cached RelyingPartyRegistration for: " + oktaRegistration.getRegistrationId());
    }

    private RelyingPartyRegistration relyingPartyRegistrationFromMetadata() {
        return RelyingPartyRegistration
                .withRegistrationId("okta") // must match what you use in URL: /saml2/authenticate/okta
                .entityId("http://www.okta.com/exkpgl3e67Uc7IuhR5d7") // your SP entity ID
                .assertionConsumerServiceLocation("http://localhost:8000/login/saml2/sso/okta") // Spring ACS URL
                .signingX509Credentials(c -> {
                    // Optional: add signing if your IdP expects signed AuthN requests
                    // c.add(Saml2X509Credential.signing(loadPrivateKey(), loadCertificate()));
                })
                .assertingPartyDetails(party -> party
                        .entityId("http://www.okta.com/exkpgl3e67Uc7IuhR5d7") // Okta IdP entity ID
                        .singleSignOnServiceLocation("https://dev-48844425.okta.com/app/dev-48844425_devauth_1/exkpgl3e67Uc7IuhR5d7/sso/saml")
                        .singleSignOnServiceBinding(Saml2MessageBinding.REDIRECT) // or Saml2MessageBinding.POST
                        .wantAuthnRequestsSigned(false) // true if Okta expects signed requests
                        .verificationX509Credentials(creds -> {
                            creds.add(Saml2X509Credential.verification(idpCertificate()));
                        })
                )
                .build();
    }

    private X509Certificate idpCertificate() {
        try (InputStream is = new ClassPathResource("idp-certificate.pem").getInputStream()) {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) factory.generateCertificate(is);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load IdP certificate from classpath", ex);
        }
    }

    @Override
    public RelyingPartyRegistration findByRegistrationId(String registrationId) {
        RelyingPartyRegistration registration = registrationMap.get(registrationId);
        System.out.println("Returning cached registration for: " + registrationId +
                " [hashCode=" + registration.hashCode() + "]");
        return registration;
    }

    @Override
    public RelyingPartyRegistration findUniqueByAssertingPartyEntityId(String entityId) {
        return RelyingPartyRegistrationRepository.super.findUniqueByAssertingPartyEntityId(entityId);
    }
}