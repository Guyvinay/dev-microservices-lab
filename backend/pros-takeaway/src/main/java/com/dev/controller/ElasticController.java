package com.dev.controller;

import com.dev.common.dto.document.Document;
import com.dev.dto.ProfilingDocumentDTO;
import com.dev.modal.Student;
import com.dev.service.ElasticService;
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

    @PostMapping(value = "/document/bulk")
    public ResponseEntity<List<ProfilingDocumentDTO>> indexBulkDocument() {
        return new ResponseEntity<>(elasticService.indexBulkDocument(), HttpStatus.OK);
    }

    @GetMapping(value = "/documents/bulk")
    public ResponseEntity<List<ProfilingDocumentDTO>> getProfilingDocuments(int from, int page) {
        return new ResponseEntity<>(elasticService.getProfilingDocuments(from, page), HttpStatus.OK);
    }


    @GetMapping
    ResponseEntity<List<ProfilingDocumentDTO>> getAllProfilingDocuments(
            String tenantId, Long moduleId, Integer pageNumber, Integer pageSize) {
        return new ResponseEntity<>(elasticService.getAllProfilingDocuments(tenantId, moduleId, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping(value = "/field")
    ResponseEntity<ProfilingDocumentDTO> getProfilingDocumentById(
            String tenantId, Long moduleId, String fieldId, Integer pageNumber, Integer pageSize) {
        return new ResponseEntity<>(elasticService.getProfilingDocumentById(tenantId, moduleId, fieldId, pageNumber, pageSize), HttpStatus.OK);
    }


}
