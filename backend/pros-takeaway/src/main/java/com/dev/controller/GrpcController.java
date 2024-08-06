package com.dev.controller;

import com.dev.common.dto.document.Document;
import com.dev.common.dto.document.DocumentSearchRequestDTO;
import com.dev.grpc.GrpcClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/grpc")
public class GrpcController {

    @Autowired
    private GrpcClientServiceImpl grpcClientService;

    @GetMapping
    public void sendGrpcRequest() {
        grpcClientService.getGrpcResponse();
    }


    @GetMapping(value = "/documents")
    public ResponseEntity<List<Document>> getAllDocuments() {
        return new ResponseEntity<>(grpcClientService.getAllDocuments(), HttpStatus.OK);
    }

    @PostMapping(value = "/documents")
    public ResponseEntity<List<Document>> getDocumentsByQueries(@RequestBody DocumentSearchRequestDTO documentSearchRequestDTO) {
        return new ResponseEntity<>(grpcClientService.getDocumentsByQueries(documentSearchRequestDTO), HttpStatus.OK);
    }

}
