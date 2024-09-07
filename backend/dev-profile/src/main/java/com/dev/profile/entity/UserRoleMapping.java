package com.dev.profile.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "user_role_mappings")
public class UserRoleMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private UserRole userRole;
}
