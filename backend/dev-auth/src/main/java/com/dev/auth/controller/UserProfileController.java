package com.dev.auth.controller;

import com.dev.auth.dto.UserProfileRequestDTO;
import com.dev.auth.dto.UserProfileResponseDTO;
import com.dev.auth.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1.0/users")
@RequiredArgsConstructor
@Tag(name = "User Profile Management", description = "Endpoints for managing user profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user profile and returns the created user details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<UserProfileResponseDTO> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User details to create a new user", required = true)
            @Valid @RequestBody UserProfileRequestDTO request) {
        UserProfileResponseDTO response = userProfileService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Gets all users",
            description = "Returns all the user details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User details fetched successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public List<UserProfileResponseDTO> getAllUsers() {
        return userProfileService.getAllUsers();
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves the user profile by the given ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponseDTO> getUserById(
            @PathVariable @Schema(description = "UUID of the user", example = "550e8400-e29b-41d4-a716-446655440000") UUID id) {
        UserProfileResponseDTO response = userProfileService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update user details",
            description = "Updates an existing user profile with the provided details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponseDTO> updateUser(
            @PathVariable @Schema(description = "UUID of the user", example = "550e8400-e29b-41d4-a716-446655440000") UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated user details", required = true)
            @RequestBody UserProfileRequestDTO request) {
        UserProfileResponseDTO response = userProfileService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete a user",
            description = "Deletes a user profile by the given ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable @Schema(description = "UUID of the user", example = "550e8400-e29b-41d4-a716-446655440000") UUID id) {
        userProfileService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
