package com.dev.multiTanent;

import org.checkerframework.checker.initialization.qual.Initialized;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.UnknownKeyFor;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;

public class SchemaBasedMultiTenantConnectionProvider implements MultiTenantConnectionProvider<String> {
    @Override
    public Connection getAnyConnection() throws SQLException {
        return null;
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {

    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        return null;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {

    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public @UnknownKeyFor @NonNull @Initialized boolean isUnwrappableAs(@UnknownKeyFor @NonNull @Initialized Class<@UnknownKeyFor @NonNull @Initialized ?> unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(@UnknownKeyFor @NonNull @Initialized Class<T> unwrapType) {
        return null;
    }
}
