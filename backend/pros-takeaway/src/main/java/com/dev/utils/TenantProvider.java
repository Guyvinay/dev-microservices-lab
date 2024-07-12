package com.dev.utils;

import com.dev.rmq.utility.RabbitTenantProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TenantProvider implements RabbitTenantProvider {

    private static final Logger log = LoggerFactory.getLogger(TenantProvider.class);

    @Override
    public Set<String> getAllTenants() {
        Set<String> tenants = new HashSet<>();
        tenants.add("vinay");
//        tenants.add("vinay2");
//        tenants.add("vinay1");
//        tenants.add("vHost2");
//        tenants.add("vHost3");
//        tenants.add("vHost4");
//        tenants.add("vHost5");
        log.info("tenants {}", tenants);
        return tenants;
    }
}
