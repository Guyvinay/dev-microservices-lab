package com.dev.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.envers.Audited;


@Entity
@Table(name = "ORGANIZATION_INFO_MODEL")
@Audited
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationModel {

    @Id
    @Column(name = "ORGANIZATION_ID")
    private Long organizationId;

    @Column(name = "CONTACT_NUMBER")
    private Long contactNumber;

    @Column(name = "TENANT_ID", columnDefinition = "nvarchar(100)")
    private String tenantId;

    @Column(name = "LOGO_URL")
    private String logoUrl;

    @Column(name = "NAME")
    private String name;

    @Column(name = "WEBSITE_URL")
    private String websiteUrl;

    @Column(name = "INDUSTRY_TYPE")
    private String industryType;

    @Column(name = "BILLING_EMAIL")
    private String billingEmail;

    @Column(name = "CREATED_AT")
    private long createdAt;

    @Column(name = "UPDATED_AT")
    private long updatedAt;

    @Column(name = "CREATED_BY", columnDefinition = "nvarchar(150)")
    private String createdBy;

    @Column(name = "UPDATED_BY", columnDefinition = "nvarchar(150)")
    private String updatedBy;
}
