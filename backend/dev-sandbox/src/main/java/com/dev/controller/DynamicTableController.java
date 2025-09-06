package com.dev.controller;


import com.dev.dto.jooqdefinition.TableDefinition;
import com.dev.service.dynamictable.DynamicTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/dynamic-table")
public class DynamicTableController {

    @Autowired
    private DynamicTableService dynamicTableService;

    @PostMapping(value = "/create-table/{tableName}")
    public ResponseEntity<String> createTable(@PathVariable String tableName) {

        TableDefinition definition = new TableDefinition();
        definition.setName(tableName);
        definition.setVarcharColumns(List.of("first_name", "last_name", "address"));
//        definition.setPks();
        dynamicTableService.createTable("vinay", definition);
        return new ResponseEntity<>("created", HttpStatus.OK);
    }

}
