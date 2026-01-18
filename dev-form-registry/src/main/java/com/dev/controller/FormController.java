package com.dev.controller;

import com.dev.service.FormPublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
public class FormController {

    private final FormPublishService publishService;

    @PostMapping("/{id}/publish")
    public ResponseEntity<String> publish(@PathVariable UUID id) {

        publishService.publishForm(id);

        return ResponseEntity.ok("published");
    }
}
