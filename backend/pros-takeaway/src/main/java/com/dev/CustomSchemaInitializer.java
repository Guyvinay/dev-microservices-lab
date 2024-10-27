package com.dev;

import com.dev.hibernate.SchemaInitializer;
import com.dev.hibernate.service.DatasourceService;
import com.dev.service.DataService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;


@Component
public class CustomSchemaInitializer implements SchemaInitializer {

    @Autowired
    private DatasourceService datasourceService;

    @Override
    public void initialize(String tenantId) {
        try {
            datasourceService.isSchemaExists("vinay");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
