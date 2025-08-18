package com.dev.saml.repository;

import com.dev.saml.entity.SamlRelyingPartyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SamlRelyingPartyJpaRepository extends JpaRepository<SamlRelyingPartyEntity, Long> {
    Optional<SamlRelyingPartyEntity> findByRegistrationId(String registrationId);
}