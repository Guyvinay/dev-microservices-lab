package com.dev.saml.service;

import com.dev.saml.entity.SamlRelyingPartyEntity;
import com.dev.saml.repository.SamlRelyingPartyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.stereotype.Service;
import com.dev.saml.util.PemUtils;


import java.security.cert.X509Certificate;

@Service
@RequiredArgsConstructor
public class SamlRelyingPartyConfigService {
    private final SamlRelyingPartyJpaRepository repo;

    public RelyingPartyRegistration getByRegistrationId(String registrationId) {
        SamlRelyingPartyEntity e = repo.findByRegistrationId(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("No SAML config for " + registrationId));
        X509Certificate spCert = PemUti.parseCertificate(e.getSigningCertificate());

    }


}
