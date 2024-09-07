package com.dev.profile.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Data
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private boolean enabled;

    @OneToMany(mappedBy = "userProfile", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UserRoleMapping> userRoles;


}
