package com.dev.auth.dto;

import com.dev.auth.entity.*;
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
    private OrganizationTenantMapping organizationTenantMapping;
    private UserProfileResponseDTO userProfileModel;
    private UserProfileTenantMapping userProfileTenantMapping;
    private UserProfileRoleModel userProfileRoleModel;
    private UserProfileRoleMapping userProfileRoleMapping;
}
