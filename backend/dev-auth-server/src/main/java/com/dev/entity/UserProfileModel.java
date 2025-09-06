package com.dev.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.time.Instant;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "USER_PROFILE_MODEL")
@Audited
@AuditTable("USER_PROFILE_MODEL_AUD")
@ApiModel(description = "Represents a user profile in the system.")
@Builder
public class UserProfileModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", updatable = false, nullable = false)
    @ApiModelProperty(name = "id", value = "Unique identifier of the user", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Column(name = "PASSWORD")
    @ApiModelProperty(name = "password", value = "Hashed password for the user", example = "$2a$10$...")
    private String password;

    @Column(name = "EMAIL", nullable = false, unique = true)
    @ApiModelProperty(name = "email", value = "Email address of the user", example = "john.doe@example.com")
    private String email;

    @Column(name = "NAME", nullable = false)
    @ApiModelProperty(name = "Name", value = "Name of the user", example = "John")
    private String name;

    @Column(name = "IS_ACTIVE", nullable = false)
    @ApiModelProperty(name = "isActive", value = "Indicates if the user account is active", example = "true")
    private boolean isActive;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @ApiModelProperty(name = "createdAt", value = "Timestamp when the user profile was created", example = "1705741200000")
    private Long createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    @ApiModelProperty(name = "updatedAt", value = "Timestamp when the user profile was last updated", example = "1705741300000")
    private Long updatedAt;

    @Transient
    private Set<Long> roles;


    @PrePersist
    protected void onCreate() {
        long timestamp = Instant.now().toEpochMilli();
        this.createdAt = timestamp;
        this.updatedAt = timestamp;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now().toEpochMilli();
    }

    public Set<String> getRoles(){
        if(roles != null) {
            return roles.stream().map(id -> ((Long) id).toString()).collect(Collectors.toSet());
        }
        return Set.of();
    }
}
/**
 {
 "id": "550e8400-e29b-41d4-a716-446655440000",
 "username": "john_doe",
 "password": "$2a$10$...",
 "email": "john.doe@example.com",
 "firstName": "John",
 "lastName": "Doe",
 "isActive": true,
 "createdAt": 1705741200000,
 "updatedAt": 1705741300000
 }
 */