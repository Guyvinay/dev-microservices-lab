package com.pros.utils;

import jakarta.annotation.PostConstruct;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class CreateCustomSchema {

    Logger logger = LoggerFactory.getLogger(CreateCustomSchema.class);

    @Autowired
    private DataSource dataSource;
//    @PostConstruct
    public void createSchema() throws SQLException {
        logger.info("Schema Creation starts");
        Connection connection = this.dataSource.getConnection();
        Statement statement = connection.createStatement();
        PGobject pGobject = new PGobject();
        pGobject.setType("text");
        pGobject.setValue("Vinay1");
        String sql  = "CREATE SCHEMA \"" + pGobject.toString() + "\"";
        statement.execute(sql);
        logger.info("Schema Created");
    }

}
