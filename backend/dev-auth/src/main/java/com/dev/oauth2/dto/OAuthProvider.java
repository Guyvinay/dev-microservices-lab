package com.dev.oauth2.dto;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "OAUTH_PROVIDERS")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OAuthProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_ID", nullable = false)
    private String userId; // Foreign key reference to users.id

    @Column(nullable = false)
    private String provider; // e.g., "github", "google"

    @Column(name = "PROVIDER_ID", nullable = false)
    private String providerId; // Unique ID from OAuth provider
}
