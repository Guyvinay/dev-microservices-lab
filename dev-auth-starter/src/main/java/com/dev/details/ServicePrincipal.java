package com.dev.details;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ServicePrincipal {

    private final String serviceName;
    private final List<String> scopes;
}
