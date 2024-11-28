package com.dev.modal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Data
public class JWTToken implements Serializable {

    @Serial
    private static final long serialVersionUID = 5707018833649747987L;

    @ApiModelProperty(name = "username", value = "MDO internal userid ..")
    private String userId;

    @ApiModelProperty(name = "roles", value = "Set of the roles associated with user")
    private Set<String> roles;

    @ApiModelProperty(name = "tenantCode", value = "TenantId of the user")
    private String tenantCode;

    @ApiModelProperty(name = "email", value = "Email for the user")
    private String email;

    @ApiModelProperty(name = "userId", value = "name the user")
    private String name;
}