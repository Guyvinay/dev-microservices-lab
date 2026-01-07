package com.dev.controller;


import com.dev.utility.DatasetGenerator;
import com.dev.service.impl.DatasetPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/datasets")
@RequiredArgsConstructor
public class DatasetController {

    private final DatasetPublisherService publisherService;
    private final DatasetGenerator datasetGenerator;

    @GetMapping("/create")
    public String createDataset() throws Exception {
        datasetGenerator.generate();
        return "Dataset created";
    }

    @GetMapping("/publish")
    public String publishDataset() throws Exception {
        publisherService.publishDataset();
        return "Publish complete";
    }

}