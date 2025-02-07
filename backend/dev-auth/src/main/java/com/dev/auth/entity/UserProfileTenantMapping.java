package com.dev.auth.entity;

import lombok.*;
import org.hibernate.envers.Audited;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "USER_TENANT_MAPPING")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileTenantMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private UUID id;

    @Column(name = "TENANT_ID", nullable = false)
    private String tenantId;

    @Column(name = "USER_ID", nullable = false)
    private UUID userId;

    @Column(name = "ORGANIZATION_ID", nullable = false)
    private UUID organizationId;
}