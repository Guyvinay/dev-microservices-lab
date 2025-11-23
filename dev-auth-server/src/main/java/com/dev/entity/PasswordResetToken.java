package com.dev.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "PASSWORD_RESET_TOKEN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 150)
    private String tokenHash;

    @Column(nullable = false)
    private long expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @Column(nullable = false)
    private boolean readyToUse = false;

    @Column(nullable = false)
    private long createdAt = Instant.now().toEpochMilli();

}
