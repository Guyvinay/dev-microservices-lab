package com.dev.auth.dto;

import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenDto {

    private UUID userId;
    private String org;
    private String name;
    private String email;
    private String tenantId;
    private Date createdDate;
    private Date expiryDate;
    private List<String> roles;

}


