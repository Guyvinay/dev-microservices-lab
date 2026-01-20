package com.dev.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "short_urls")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShortUrlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false, length = 64)
    private String tenantId;

    @Column(name = "short_code", nullable = false, length = 16)
    private String shortCode;

    @Column(name = "long_url", nullable = false, columnDefinition = "TEXT")
    private String longUrl;

    @Column(name = "created_by", length = 128)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private long createdAt;

    @Column(name = "expires_at")
    private long expiresAt;

    @Column(name = "max_hits")
    private Long maxHits;

    @Column(name = "hit_count", nullable = false)
    private Long hitCount = 0L;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
    // getters, setters
}
