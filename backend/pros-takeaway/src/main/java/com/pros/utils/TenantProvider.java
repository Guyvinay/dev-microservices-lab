package com.pros.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TenantProvider implements TenantRetriever {


    private static final Logger log = LoggerFactory.getLogger(TenantProvider.class);

    @Override
    public Set<String> getAllTenants() {
        Set<String> tenants = new HashSet<>();
        tenants.add("vinay");
//        tenants.add("vHost2");
//        tenants.add("vHost3");
//        tenants.add("vHost4");
//        tenants.add("vHost5");
        log.info("tenants {}", tenants);
        return tenants;
    }
}
