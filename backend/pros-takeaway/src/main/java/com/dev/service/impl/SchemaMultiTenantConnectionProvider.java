package com.dev.service.impl;

import com.dev.hibernate.multiTanent.TenantIdentifierResolver;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;


import javax.sql.DataSource;
import java.util.Map;

public class SchemaMultiTenantConnectionProvider  extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String> implements HibernatePropertiesCustomizer {

    private final DataSource dataSource;

    public SchemaMultiTenantConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected DataSource selectAnyDataSource() {
        // Return the shared datasource for any tenant
        return this.dataSource;
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        return this.dataSource;
    }


    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        // Use the correct Hibernate constant for setting the connection provider
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }
}