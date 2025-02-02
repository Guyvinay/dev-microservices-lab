package com.dev.auth.dto;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    private String username;
    private String password;
    private String orgId;
}
