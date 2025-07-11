package com.dev.saml.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.Saml2MessageBinding;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.security.saml2.core.Saml2X509Credential.Saml2X509CredentialType.SIGNING;

@Component
public class CustomRelyingPartyRegistrationRepository implements RelyingPartyRegistrationRepository {

    private final Map<String, RelyingPartyRegistration> registrationMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        RelyingPartyRegistration oktaRegistration = samlRegistrationRepository();
        registrationMap.put(oktaRegistration.getRegistrationId(), oktaRegistration);
        System.out.println("Cached RelyingPartyRegistration for: " + oktaRegistration.getRegistrationId());
    }

    public RelyingPartyRegistration samlRegistrationRepository() {
        String keystorePassword = "changeit";
        String keyAlias = "saml-key";

        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (InputStream is = new ClassPathResource("saml/saml-keystore.jks").getInputStream()) {
                keyStore.load(is, keystorePassword.toCharArray());
            }

            X509Certificate certificate = (X509Certificate) keyStore.getCertificate(keyAlias);
            RSAPrivateKey privateKey = (RSAPrivateKey) keyStore.getKey(keyAlias, keystorePassword.toCharArray());

            Saml2X509Credential signingCredential = new Saml2X509Credential(privateKey, certificate, SIGNING);

            RelyingPartyRegistration registration = RelyingPartyRegistration
                    .withRegistrationId("okta")
                    .entityId("http://localhost:8000/saml2/service-provider-metadata/okta")
                    .assertionConsumerServiceLocation("http://localhost:8000/login/saml2/sso/okta")
                    .signingX509Credentials(c -> c.add(signingCredential))
                    .assertingPartyDetails(party -> party
                            .entityId("http://www.okta.com/exkpgl3e67Uc7IuhR5d7")
                            .singleSignOnServiceLocation("https://dev-48844425.okta.com/app/dev-48844425_devauth_1/exkpgl3e67Uc7IuhR5d7/sso/saml")
                            .wantAuthnRequestsSigned(true)
                            .verificationX509Credentials(c -> {
                                // you must also add the IdP's cert here
                                X509Certificate idpCert = null; // path in classpath
                                try {
                                    idpCert = loadCertificate("saml/okta-certificate.cert");
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                c.add(new Saml2X509Credential(idpCert, Saml2X509Credential.Saml2X509CredentialType.VERIFICATION));
                            })
                    )
                    .build();
            return registration;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public static X509Certificate loadCertificate(String path) throws Exception {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            return (X509Certificate) factory.generateCertificate(is);
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
        try (InputStream is = new ClassPathResource("saml/idp-certificate.pem").getInputStream()) {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) factory.generateCertificate(is);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load IdP certificate from classpath", ex);
        }
    }

}