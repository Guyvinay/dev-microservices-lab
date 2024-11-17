package com.dev.service.impl;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;

import javax.sql.DataSource;
import java.util.Map;

public class SchemaMultiTenantConnectionProvider  extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl implements HibernatePropertiesCustomizer {


    private final DataSource dataSource;

    public SchemaMultiTenantConnectionProvider(DataSource ds) {
        this.dataSource = ds;
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return this.dataSource;
    }

    @Override
    protected DataSource selectDataSource(Object tenantIdentifier) {
        return this.dataSource;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
    }
}
