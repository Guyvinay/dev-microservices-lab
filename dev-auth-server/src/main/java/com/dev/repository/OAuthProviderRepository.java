package com.dev.repository;

import com.dev.oauth2.dto.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OAuthProviderRepository extends JpaRepository<OAuthProvider, Long> {
    List<OAuthProvider> findByUserId(String userId);
    Optional<OAuthProvider> findByProviderAndProviderId(String provider, String providerId);
    Optional<OAuthProvider> findByUserIdAndProviderId(String userId, String providerId);
}