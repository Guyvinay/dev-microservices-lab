package com.dev.utils;

import com.dev.hibernate.service.DatasourceService;
import com.dev.utility.RabbitTenantProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class TenantProvider implements RabbitTenantProvider {

    private static final Logger log = LoggerFactory.getLogger(TenantProvider.class);

    @Autowired
    private DatasourceService datasourceService;

    @Override
    public Set<String> getAllTenants() {
        Set<String> tenants = new HashSet<>();
        tenants.add("public");
        List<String> excludeSchema = datasourceService.getExcludeSchema();
        try {
            for (String tenant: datasourceService.getAllTenanats()) {
                if(!excludeSchema.contains(tenant)) {
                    tenants.add(tenant);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        log.info("tenants {}", tenants);
        return tenants;
    }
}
