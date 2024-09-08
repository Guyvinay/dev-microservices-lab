package com.dev.profile.controller;

import com.dev.profile.entity.Authority;
import com.dev.profile.service.AuthorityService;
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
    @PostMapping
    public ResponseEntity<Authority> createAuthority(@RequestBody Authority authority) {
        Authority createdAuthority = authorityService.createAuthority(authority);
        return new ResponseEntity<>(createdAuthority, HttpStatus.CREATED);
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
