package com.pover.Library.controller;

import com.pover.Library.dto.*;
import com.pover.Library.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user account in the system with the provided details."
    )

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser (@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.createUser(userRequestDto);
        return new ResponseEntity<>(userResponseDto, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all users in the system."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll(){
        List<UserResponseDto> users = userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves the details of a specific user based on the user ID."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable long id){
        UserResponseDto userResponseDto = userService.getUserById(id);
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes user from database based on provided ID"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }

    @Operation(
            summary = "Login user",
            description = "Authenticates a user based on member number, password, and role, returning a token if successful."
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String personalNumber = credentials.get("personal_number");
        String password = credentials.get("password");

        if (personalNumber == null || password == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Missing required fields: member number, password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        Optional<String> token = userService.authenticateUser(personalNumber, password);

        if (token.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("token", token.get());
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid credentials or role");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }


    // CONTROLLERS FOR USER PROFILE CONTROLLED BY USER
    // user's id isn't needed because of token
    // the profile of the currently authenticated user will be returned

    @Operation(
            summary = "Get user profile",
            description = "Retrieves the profile of the currently authenticated user based on the provided JWT token."
    )

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/profile")
    public ResponseEntity<BasicUserProfileResponseDto> getUserProfile(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        BasicUserProfileResponseDto userProfile = userService.getUserProfile(token);
        return ResponseEntity.ok(userProfile);
    }


    @Operation(
            summary = "Update user profile",
            description = "Updates the profile of the currently authenticated user based on the provided JWT token and new data."
    )

    @PutMapping("/profile")
    public ResponseEntity<BasicUserProfileResponseDto> updateUserProfile(@RequestHeader("Authorization") String token,
                                                                         @RequestBody BasicUserProfileRequestDto basicUserProfileRequestDto) {

        String jwtToken = token.substring(7);
        BasicUserProfileResponseDto updatedProfile = userService.updateUserProfile(jwtToken, basicUserProfileRequestDto);
        return ResponseEntity.ok(updatedProfile);
    }

    @Operation(
            summary = "Logout user",
            description = "Logs out the currently authenticated user based on the provided JWT token."
    )

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean isLoggedOut = userService.logout(token);

            if (isLoggedOut) {
                return new ResponseEntity<>("Successfully logged out", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Invalid logout request", HttpStatus.BAD_REQUEST);
    }
}

