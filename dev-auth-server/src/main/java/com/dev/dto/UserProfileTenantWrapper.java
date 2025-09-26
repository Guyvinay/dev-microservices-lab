package com.dev.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserProfileTenantWrapper {

    private UserProfileResponseDTO profileResponseDTO;
    private UserProfileTenantDTO savedProfileTenantMapping;
}
