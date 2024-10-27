package com.dev;

import com.dev.hibernate.SchemaInitializer;
import com.dev.hibernate.service.DatasourceService;
import liquibase.exception.LiquibaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.sql.SQLException;


@Component(value = "custom-schema-initializer")
//@DependsOn("spring-liquibase")
//@AutoConfigureAfter(DatasourceService.class)
public class CustomSchemaInitializer implements SchemaInitializer {

    @Autowired
    private DatasourceService datasourceService;

    @Override
    public void initialize(String tenantId) {
        try {
            datasourceService.isSchemaExists(tenantId);
            datasourceService.createSchema(tenantId);
            datasourceService.executeLiquibase(tenantId);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }
}
