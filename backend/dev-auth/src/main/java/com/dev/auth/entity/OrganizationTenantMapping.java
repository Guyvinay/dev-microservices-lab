package com.dev.auth.entity;

import lombok.*;
import org.hibernate.envers.Audited;
import jakarta.persistence.*;

@Entity
@Table(name = "ORG_TENANT_MAPPING")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationTenantMapping {

    @Id
    @Column(name = "TENANT_ID", nullable = false, updatable = false)
    private String tenantId;

    @Column(name = "ORGANIZATION_ID", nullable = false)
    private Long organizationId;

    @Column(name = "TENANT_NAME", nullable = false)
    private String tenantName;

    @Column(name = "CREATED_AT", updatable = false)
    private long createdAt;

    @Column(name = "UPDATED_AT", updatable = false)
    private long updatedAt;

    @Column(name = "IS_ACTIVE", nullable = false)
    private boolean active;

    @Column(name = "DEFAULT_LANGUAGE")
    private String defaultLanguage;

    @Column(name = "IS_MASTER_TENANT")
    private Boolean masterTenant;

}
