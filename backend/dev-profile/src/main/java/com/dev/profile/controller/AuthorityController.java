package com.dev.profile.controller;

import com.dev.profile.dto.AuthorityDTO;
import com.dev.profile.entity.Authority;
import com.dev.profile.service.AuthorityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1.0/authority")
public class AuthorityController {

    @Autowired
    private AuthorityService authorityService;

    // Create a new Authority
    @Operation(summary = "Create a new user profile", description = "This endpoint creates a new user profile with roles and authorities.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User profile created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<AuthorityDTO> createAuthority(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Authority object that needs to be created")
            @RequestBody AuthorityDTO authority) {
        return new ResponseEntity<>(authorityService.createAuthority(authority), HttpStatus.CREATED);
    }

    // Get an Authority by ID
    @GetMapping("/{id}")
    public ResponseEntity<Authority> getAuthorityById(@PathVariable UUID id) {
        Authority authority = authorityService.getAuthorityById(id);
        return new ResponseEntity<>(authority, HttpStatus.OK);
    }

    // Update an existing Authority
    @PutMapping("/{id}")
    public ResponseEntity<Authority> updateAuthority(@PathVariable UUID id, @RequestBody Authority authorityDetails) {
        Authority updatedAuthority = authorityService.updateAuthority(id, authorityDetails);
        return new ResponseEntity<>(updatedAuthority, HttpStatus.OK);
    }

    // Get all Authorities
    @GetMapping
    public ResponseEntity<List<Authority>> getAllAuthorities() {
        List<Authority> authorities = authorityService.getAllAuthorities();
        return new ResponseEntity<>(authorities, HttpStatus.OK);
    }

    // Delete an Authority by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthority(@PathVariable UUID id) {
        authorityService.deleteAuthority(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
