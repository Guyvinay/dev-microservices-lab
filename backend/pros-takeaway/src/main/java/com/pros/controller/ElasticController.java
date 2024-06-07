package com.pros.controller;

import com.pros.modal.Student;
import com.pros.modal.Tech;
import com.pros.service.ElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/elastic")
public class ElasticController {


    @Autowired
    private ElasticService elasticService;

    @PostMapping(value = "/student")
    public void saveStudent(@RequestBody Student student) throws IOException {

        elasticService.saveStudent(student);
    }

}
