package com.dev.auth.service;

import com.dev.auth.dto.LoginRequestDTO;

public interface AuthService {

    String login(LoginRequestDTO loginRequestDTO);

}
