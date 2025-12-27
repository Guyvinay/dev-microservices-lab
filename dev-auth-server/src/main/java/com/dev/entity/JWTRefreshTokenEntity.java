package com.dev.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "JWT_REFRESH_TOKENS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JWTRefreshTokenEntity {

    @Id
    private UUID jti;
    private UUID userId;
    private boolean revoked;
    private UUID replacedByJti;
    private long expiresAt;
    private long createdAt;
    private Long revokedAt;
}
