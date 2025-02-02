package com.dev.auth.entity;

import lombok.*;
import org.hibernate.envers.Audited;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "USER_TENANT_MAPPING")
@Audited
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileTenantMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "UUID", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "TENANT_ID", columnDefinition = "NVARCHAR(100)", nullable = false)
    private String tenantId;

    @Column(name = "USER_ID", columnDefinition = "NVARCHAR(150)", nullable = false)
    private String userId;

    @Column(name = "CREATED_AT", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "ORGANIZATION_ID", columnDefinition = "NVARCHAR(100)", nullable = false)
    private String organizationId;
}