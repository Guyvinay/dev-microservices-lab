package com.dev.controller;

import com.dev.CustomSchemaInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/datasource")
public class DataSourceController {

    @Autowired
    private CustomSchemaInitializer customSchemaInitializer;

    @GetMapping(value = "/{tenantId}")
    public void initializeSchema(@PathVariable("tenantId") String tenantId) {
        customSchemaInitializer.initialize(tenantId);
    }

    @GetMapping()
    public ResponseEntity<List<String>> getAllTenants() {
        try {
            return new ResponseEntity<>(customSchemaInitializer.getAllTenants(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

}
