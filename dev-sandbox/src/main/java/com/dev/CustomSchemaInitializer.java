package com.dev;

import com.dev.hibernate.SchemaInitializer;
import com.dev.hibernate.service.DatasourceService;
import liquibase.exception.LiquibaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;


@Component(value = "custom-schema-initializer")
@DependsOn("spring-liquibase")
//@AutoConfigureAfter(DatasourceService.class)
@Slf4j
public class CustomSchemaInitializer implements SchemaInitializer {

    @Autowired
    private DatasourceService datasourceService;

    @Override
    public void initialize(String tenantId) {

        try {
            log.info("initialization called for: {}", tenantId);
            if(!datasourceService.isSchemaExists(tenantId)) {
                datasourceService.createSchema(tenantId);
                datasourceService.executeLiquibase(tenantId);
            } else {
                log.info("Schema already exists for: {}", tenantId);
            }
            log.info("initialization complete for: {}", tenantId);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllTenants() throws SQLException {
        List<String> allTenants = datasourceService.getAllTenanats();
        log.info("all tenants: [{}]", allTenants);
        return allTenants;
    }

    public List<String> getExcludeSchema(){
        return datasourceService.getExcludeSchema();
    }

}
