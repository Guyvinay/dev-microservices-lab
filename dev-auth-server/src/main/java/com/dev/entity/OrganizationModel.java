package com.dev.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.util.UUID;


@Entity
@Table(name = "ORGANIZATION_INFO_MODEL")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ORG_ID")
    private UUID orgId;

    @Column(name = "ORG_CONTACT")
    private Long orgContact;

    @Column(name = "ORG_NAME", unique = true, nullable = false)
    private String orgName;

    @Column(name = "ORG_EMAIL", unique = true, nullable = false)
    private String orgEmail;

    @Column(name = "CREATED_AT")
    private Long createdAt;

    @Column(name = "UPDATED_AT")
    private Long updatedAt;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_BY")
    private String updatedBy;
}
