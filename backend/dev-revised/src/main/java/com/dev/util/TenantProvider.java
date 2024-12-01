package com.dev.util;
import com.dev.rmq.utility.RabbitTenantProvider;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TenantProvider implements RabbitTenantProvider {
    @Override
    public Set<String> getAllTenants() {
        Set<String> tenants = new HashSet<>();
        tenants.add("vinay");
//        tenants.add("vinay2");
        return tenants;
    }
}
