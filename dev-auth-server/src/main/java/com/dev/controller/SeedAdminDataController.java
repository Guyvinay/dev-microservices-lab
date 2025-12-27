package com.dev.controller;

import com.dev.dto.exception.GeneralResponseDTO;
import com.dev.service.SystemBootstrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/seed")
@RequiredArgsConstructor
public class SeedAdminDataController {

    private final SystemBootstrapService bootstrapService;

    @GetMapping
    public ResponseEntity<GeneralResponseDTO<String>> getAllOrganizations() {
        bootstrapService.bootstrap();
        return ResponseEntity.ok(GeneralResponseDTO.ok("Data Seeded"));
    }
}
