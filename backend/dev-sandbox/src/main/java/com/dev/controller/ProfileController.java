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

    @GetMapping(value = "/{id}")
    public ResponseEntity<Profile> getProfileById(@PathVariable("id") String id) throws IOException {
        return new ResponseEntity<Profile>(profileService.findById(id), HttpStatus.ACCEPTED);
    }

    @GetMapping()
    public ResponseEntity<List<Profile>> GetAllProfiles() throws IOException {
        return new ResponseEntity<List<Profile>>(profileService.findAllProfiles(), HttpStatus.ACCEPTED);
    }

}
