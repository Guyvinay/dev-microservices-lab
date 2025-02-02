package com.dev.auth.entity;

import lombok.*;
import org.hibernate.envers.Audited;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "USER_ROLES")
@Audited
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRoleMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "UUID", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "USER_ID", columnDefinition = "NVARCHAR(150)", nullable = false)
    private String userId;

    @Column(name = "ROLE_ID", nullable = false)
    private Long roleId;

    @Column(name = "IS_DEFAULT", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean defaultRole = false;

    @Column(name = "TENANT_ID", columnDefinition = "NVARCHAR(100)", nullable = false)
    private String tenantId;
}
