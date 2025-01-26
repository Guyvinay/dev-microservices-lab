package com.dev.auth.controller;

import com.dev.auth.dto.LoginRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(HttpServletRequest request) {
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }


}
