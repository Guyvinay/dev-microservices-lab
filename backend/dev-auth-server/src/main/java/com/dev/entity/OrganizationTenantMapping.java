package com.dev.entity;

import lombok.*;
import org.hibernate.envers.Audited;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "ORG_TENANT_MAPPING")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationTenantMapping {

    @Id
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "ORG_ID", nullable = false)
    private UUID orgId;

    @Column(name = "TENANT_NAME", nullable = false, unique = true)
    private String tenantName;

    @Column(name = "TENANT_ACTIVE", nullable = false)
    private boolean tenantActive;

    @Column(name = "CREATED_AT")
    private long createdAt;

    @Column(name = "UPDATED_AT")
    private long updatedAt;
}
