package com.dev.controller;


import com.dev.service.FormPublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/form")
@RequiredArgsConstructor
public class FormController {

    public final FormPublishService formPublishService;


    @GetMapping(value = "publish/{formId}")
    public ResponseEntity<String> publishForm(@PathVariable(value = "formId") Long formId) {

        formPublishService.publishForm(formId);
        return ResponseEntity.ok("PUBLISHED");
    }

}
