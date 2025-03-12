package com.dev.auth.graphql.controller;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.dev.auth.dto.UserProfileResponseDTO;
import com.dev.auth.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

//@Component
@Slf4j
@Controller
public class UserProfileGraphQLResolver implements GraphQLQueryResolver {

    private final UserProfileService userProfileService;

    public UserProfileGraphQLResolver(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    // GET ALL USERS
    @QueryMapping(name = "getAllUsers")  // Explicitly maps to the schema
    public List<UserProfileResponseDTO> getAllUsers() {
        return userProfileService.getAllUsers();
    }
}
