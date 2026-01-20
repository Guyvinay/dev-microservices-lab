package com.dev.repository;

import com.dev.entity.ShortUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrlEntity, Long> {

    Optional<ShortUrlEntity> findByTenantIdAndShortCodeAndActiveTrue(
            String tenantId,
            String shortCode
    );

    boolean existsByTenantIdAndShortCode(String tenantId, String shortCode);
}

