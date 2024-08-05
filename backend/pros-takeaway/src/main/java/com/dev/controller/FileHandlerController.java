package com.dev.controller;


import com.dev.file.FileHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/files")
@CrossOrigin("*")
//@CrossOrigin(origins = "https://obscure-cod-j644xw5w6qr376p-4200.app.github.dev")
public class FileHandlerController {

    @Autowired
    private FileHandlerService fileHandlerService;

    @GetMapping(value = "/{name}")
    public ResponseEntity<Resource> getZipFiles(@PathVariable("name") String name) {
        Resource resource = fileHandlerService.getFilesAsZip(name);
        String fileName = "TemplateDownload_" + name + ".zip";
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename: "+fileName).contentType(MediaType.parseMediaType("application/json")).body(resource);
    }
}
