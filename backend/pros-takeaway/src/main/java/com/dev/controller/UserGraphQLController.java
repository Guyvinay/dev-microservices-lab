package com.dev.controller;

import com.dev.dto.UserDTO;
import com.dev.dto.UserInputDTO;
import com.dev.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserGraphQLController {

    @Autowired
    private UserService userService;

    @QueryMapping("getAllUsers")
    public List<UserDTO> getAllUsers() {
        return userService.findAll();
    }

    @QueryMapping("getUserById")
    public UserDTO getUserById(Long id) {
        return userService.findById(id);
    }

    @MutationMapping("createUser")  // Mutation to update a student
    public UserDTO createUser(UserInputDTO input) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(input.getName());
        userDTO.setEmail(input.getEmail());
        userDTO.setPassword(input.getPassword());
        return userService.createUser(userDTO);
    }

    @MutationMapping("updateUser")  // Mutation to update a student
    public UserDTO updateUser(Long id, UserInputDTO input) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(input.getName());
        userDTO.setEmail(input.getEmail());
        userDTO.setPassword(input.getPassword());
        return userService.updateUser(id, userDTO);
    }

    @MutationMapping("deleteUser")  // Mutation to update a student
    public String deleteUser(Long id) {
        userService.deleteUser(id);
        return "User deleted successfully";
    }
}
