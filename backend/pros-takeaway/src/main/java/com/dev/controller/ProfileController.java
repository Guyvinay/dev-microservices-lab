package com.dev.controller;

import com.dev.modal.Profile;
import com.dev.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(value = "/api/profiles")
@Slf4j
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @PostMapping
    public ResponseEntity<Profile> saveProfiles(@RequestBody Profile profile) throws IOException {

        String successPayload = new String(Files.readAllBytes(Paths.get("payloads/DqPayloadSuccess.json").toAbsolutePath()));

        JSONObject jsonObject = new JSONObject(successPayload);
        String automationNumber = UUID.randomUUID().toString();

            JSONArray vcArray = jsonObject.getJSONObject("mdoRecordES")
                    .getJSONObject("hdvs")
                    .getJSONObject("FLD_545886285")
                    .getJSONArray("vc");



            JSONObject firstVcObject = vcArray.getJSONObject(0);
            firstVcObject.put("c", automationNumber);

            String jsonBody = jsonObject.toString();
        //   System.out.println("Request JSON... " + jsonBody + "\n");
        log.info("profile {}", jsonBody);
        return new ResponseEntity<Profile>(profile, HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Profile> getProfileById(@PathVariable("id") String id) throws IOException {
        return new ResponseEntity<Profile>(profileService.findById(id), HttpStatus.ACCEPTED);
    }

    @GetMapping()
    public ResponseEntity<List<Profile>> GetAllProfiles() throws IOException {
        return new ResponseEntity<List<Profile>>(profileService.findAllProfiles(), HttpStatus.ACCEPTED);
    }

}
