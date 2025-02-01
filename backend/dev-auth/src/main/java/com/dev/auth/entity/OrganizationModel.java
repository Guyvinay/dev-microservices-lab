package com.dev.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;


@Entity
@Table(name = "ORGANIZATION_INFO_MODEL")
@Audited
@Data
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
