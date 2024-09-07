package com.dev.profile.controller;

import com.dev.profile.entity.UserProfile;
import com.dev.profile.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1.0")
public class UserProfileController {
    @Autowired
    private UserProfileService userProfileService;

    // Create a new UserProfile
    @PostMapping
    public ResponseEntity<UserProfile> createUserProfile(@RequestBody UserProfile userProfile) {
        UserProfile savedProfile = userProfileService.createUserProfile(userProfile);
        return new ResponseEntity<>(savedProfile, HttpStatus.CREATED);
    }

    // Get a UserProfile by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> getUserProfileById(@PathVariable UUID id) {
        UserProfile userProfile = userProfileService.getUserProfileById(id);
        return new ResponseEntity<>(userProfile, HttpStatus.OK);
    }

    // Update an existing UserProfile by ID
    @PutMapping("/{id}")
    public ResponseEntity<UserProfile> updateUserProfile(
            @PathVariable UUID id,
            @RequestBody UserProfile userProfileDetails) {
        UserProfile updatedProfile = userProfileService.updateUserProfile(id, userProfileDetails);
        return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
    }

    // Get all UserProfiles
    @GetMapping
    public ResponseEntity<List<UserProfile>> getAllUserProfiles() {
        List<UserProfile> profiles = userProfileService.getAllUserProfiles();
        return new ResponseEntity<>(profiles, HttpStatus.OK);
    }

    // Delete a UserProfile by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable UUID id) {
        userProfileService.deleteUserProfile(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
