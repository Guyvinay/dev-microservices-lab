package com.dev.controller;

import com.dev.common.annotations.Requires;
import com.dev.dto.ApiResponse;
import com.dev.dto.UserDTO;
import com.dev.modal.Student;
import com.dev.repository.StudentRepository;
import com.dev.service.UserAuditService;
import com.dev.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserAuditService userAuditService;

    @GetMapping
    @Requires(privilege = "privilege", actions = {"actions1", "actions2", "actions3", "actions4"})
    public ApiResponse<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAll();
        return new ApiResponse<>("200", "Users retrieved successfully", users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.findById(id);
        return ResponseEntity.ok(new ApiResponse<>("200", "User retrieved successfully", userDTO));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("201", "User created successfully", createdUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(new ApiResponse<>("200", "User updated successfully", updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>("204", "User deleted successfully", null));
    }

    @GetMapping(value = "/userAudit/{userId}")
    public ResponseEntity<List<UserDTO>> getUserRevision(@PathVariable("userId") Long userId) {
        return new ResponseEntity<>(userAuditService.printUserRevisionHistory(userId), HttpStatus.OK);
    }
}