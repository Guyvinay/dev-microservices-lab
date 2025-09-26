package com.dev.bulk.controller;

import com.dev.bulk.generator.DatasetGenerator;
import com.dev.bulk.service.DatasetPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

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
