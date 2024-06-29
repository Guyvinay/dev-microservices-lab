package com.pros.util;

import com.pros.utils.TenantRetriever;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TenantProvider implements TenantRetriever {
    @Override
    public Set<String> getAllTenants() {
        Set<String> tenants = new HashSet<>();
        tenants.add("vinay");
        return tenants;
    }
}
