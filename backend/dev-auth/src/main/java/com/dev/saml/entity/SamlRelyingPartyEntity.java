package com.dev.saml.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "saml_relying_party")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SamlRelyingPartyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Logical identifier, used in URLs: /saml2/authenticate/{registrationId} */
    @Column(name = "registration_id", nullable = false, unique = true)
    private String registrationId;

    // ---- Service Provider (your app) ----
    @Column(name = "entity_id", nullable = false)
    private String entityId; // e.g., "https://auth.example.com/saml/sp"

    @Column(name = "assertion_consumer_service_url", nullable = false)
    private String assertionConsumerServiceUrl; // e.g., "https://auth.example.com/login/saml2/sso/{registrationId}"

    /** PEM contents */
    @Lob
    @Column(name = "signing_certificate")
    private String signingCertificate;  // -----BEGIN CERTIFICATE----- ... -----END CERTIFICATE-----

    @Lob
    @Column(name = "signing_private_key")
    private String signingPrivateKey;   // -----BEGIN PRIVATE KEY----- ... -----END PRIVATE KEY----- (PKCS#8)

    // ---- Identity Provider (IdP) ----
    @Column(name = "idp_entity_id", nullable = false)
    private String idpEntityId;

    @Column(name = "sso_service_url", nullable = false)
    private String ssoServiceUrl; // IdP SSO endpoint (POST binding)

    @Lob
    @Column(name = "idp_certificate")
    private String idpCertificate; // IdP verification certificate PEM
}