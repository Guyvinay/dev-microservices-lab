package com.dev.auth.entity;

import lombok.*;
import org.hibernate.envers.Audited;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "USER_PROFILE_ROLE_MAPPING")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRoleMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private UUID id;

    @Column(name = "USER_ID")
    private UUID userId;

    @Column(name = "ROLE_ID", nullable = false)
    private Long roleId;

    @Column(name = "IS_DEFAULT")
    private Boolean defaultRole = false;

    @Column(name = "TENANT_ID")
    private String tenantId;
}
