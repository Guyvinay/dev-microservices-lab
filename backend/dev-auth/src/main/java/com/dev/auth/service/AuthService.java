package com.dev.auth.service;

import com.dev.auth.dto.LoginRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;

public interface AuthService {

    String login(LoginRequestDTO loginRequestDTO) throws JsonProcessingException, JOSEException;

}
