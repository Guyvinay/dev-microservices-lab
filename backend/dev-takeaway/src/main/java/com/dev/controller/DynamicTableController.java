package com.dev.controller;


import com.dev.dto.jooqdefinition.TableDefinition;
import com.dev.service.dynamictable.DynamicTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/dynamic-table")
public class DynamicTableController {

    @Autowired
    private DynamicTableService dynamicTableService;

    @PostMapping(value = "/create-table")
    public ResponseEntity<String> createTable(@RequestBody TableDefinition tableDefinition) {
        dynamicTableService.createTable("vinay", tableDefinition);
        return new ResponseEntity<>("created", HttpStatus.OK);
    }

}
