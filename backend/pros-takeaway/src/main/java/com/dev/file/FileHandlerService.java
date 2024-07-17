package com.dev.file;

import com.dev.common.dto.FileHandlerDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.springframework.util.StreamUtils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Slf4j
public class FileHandlerService {


    public InputStreamResource getFilesAsZip(String name) {
        FileHandlerDTO fileHandlerDTO = new FileHandlerDTO();
        addStaticFields(fileHandlerDTO);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonData = gson.toJson(fileHandlerDTO);
        log.info("Json Data {} ",jsonData);
        return zipFileWithDocument(jsonData, name);
    }

    private InputStreamResource zipFileWithDocument(String jsonData, String name) {
        log.info("loading json files from template.");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("start zipping.");

        try {

            String templateFile = "Template_"+name + ".json";
            String[] classPathResources = new String[]{"documentation.txt", "sample-request.json", "sample-response.json"};
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(byteArrayOutputStream);
                zipArchiveOutputStream.putArchiveEntry(new ZipArchiveEntry(templateFile));
                zipArchiveOutputStream.write(jsonData.getBytes(StandardCharsets.UTF_8));
                zipArchiveOutputStream.closeArchiveEntry();

                for(String fileName : classPathResources) {
                    String fileClassPath = "/template/"+fileName;
                    ClassPathResource classPathResource = new ClassPathResource(fileClassPath);
                    if(classPathResource.exists()) {
                        try( InputStream inputStream = classPathResource.getInputStream()) {
                            byte[] byteContent = StreamUtils.copyToByteArray(inputStream);
                            zipArchiveOutputStream.putArchiveEntry(new ZipArchiveEntry(fileName));
                            zipArchiveOutputStream.write(byteContent);
                            zipArchiveOutputStream.closeArchiveEntry();
                        }
                    } else {
                        log.info("file not exists on classpath {}", fileName);
                    }
                }
                zipArchiveOutputStream.close();
                stopWatch.stop();
                log.info(stopWatch.prettyPrint());
                try (InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray())) {
                    return new InputStreamResource(inputStream);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    public void addStaticFields(FileHandlerDTO fileHandlerDTO) {
        String staticFieldsPath = "/template/static-fields.json";
        log.info("reading static fields from {}", staticFieldsPath);
        ClassPathResource staticFileResource = new ClassPathResource(staticFieldsPath);
        if(staticFileResource.exists()) {
            try (InputStream inputStream = staticFileResource.getInputStream()) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> staticFieldsMap =  objectMapper.readValue(inputStream, new TypeReference<>() {});
                log.info("Static Fields : {}", staticFieldsMap);
                fileHandlerDTO.setStaticFields(staticFieldsMap.get("staticFields"));
                fileHandlerDTO.setDescription(staticFieldsMap.get("description"));
                fileHandlerDTO.setControlData(staticFieldsMap.get("controlData"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void convertJsonFilesToZip() {

    }

}
