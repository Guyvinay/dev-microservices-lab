package com.dev.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;

import java.util.Map;

public interface AuthService {

    Map<String, String> login() throws JsonProcessingException, JOSEException;

}
