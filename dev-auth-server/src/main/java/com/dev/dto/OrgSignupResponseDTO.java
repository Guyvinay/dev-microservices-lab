package com.dev.dto;

import com.dev.entity.OrganizationTenantMapping;
import com.dev.entity.UserProfileRoleMapping;
import com.dev.entity.UserProfileRoleModel;
import com.dev.entity.UserProfileTenantMapping;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrgSignupResponseDTO {
    private OrganizationDTO organizationDTO;
    private OrganizationTenantDTO organizationTenantMapping;
    private UserProfileTenantWrapper userProfileModel;
    private UserProfileRoleModel userProfileRoleModel;
    private UserProfileRoleMapping userProfileRoleMapping;

}
