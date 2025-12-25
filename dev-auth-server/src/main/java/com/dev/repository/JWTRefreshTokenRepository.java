package com.dev.repository;

import com.dev.entity.JWTRefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JWTRefreshTokenRepository extends JpaRepository<JWTRefreshTokenEntity, UUID> {
    Optional<JWTRefreshTokenEntity> findByJti(UUID jti);
}
