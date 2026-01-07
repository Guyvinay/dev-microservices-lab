package com.dev.controller;

import com.dev.common.dto.GeneralResponseDTO;
import com.dev.common.dto.document.Document;
import com.dev.modal.Student;
import com.dev.service.ElasticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "ElasticController", description = "REST controller to manage Elastic requests")
@RestController
@RequestMapping(value = "/elastic")
@CrossOrigin("*")
public class ElasticController {


    @Autowired
    private ElasticService elasticService;

    @PostMapping(value = "/student")
    public void saveStudent(@RequestBody Student student) {

        elasticService.saveStudent(student);
    }

    @GetMapping(value = "/student")
    public void getAllStudent() {

        elasticService.getAllStudents();
    }

    @GetMapping(value = "/student/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable("id") String id) throws IOException {
        return new ResponseEntity<>(elasticService.findStudentById(id), HttpStatus.OK);
    }

    @GetMapping(value = "/index")
    public void indexRequest() {
        elasticService.indexRequest();
    }

    @GetMapping(value = "/document/{index}")
    public ResponseEntity<List<Document>> getAllDocuments(@PathVariable("index") String index) {
        return new ResponseEntity<List<Document>>(elasticService.getAllDocument(index), HttpStatus.OK);
    }

}
