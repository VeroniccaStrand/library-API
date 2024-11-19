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
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto userRequestDto) {
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
            summary = "Login user",
            description = "Authenticates a user based on member number and password, returning a token if successful."
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String memberNumber = credentials.get("member_number");
        String password = credentials.get("password");

        if (memberNumber == null || password == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Missing required fields: member mumber and/or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        Optional<String> token = userService.authenticateUser(memberNumber, password);
        return token
                .map(t -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("token", t);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Invalid credentials");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
                });
    }

    // CONTROLLERS FOR USER PROFILE CONTROLLED BY USER
    // user's id isn't needed because of token
    // the profile of the currently authenticated user will be returned

    @Operation(
            summary = "Get user profile",
            description = "Retrieves the profile of the currently authenticated user based on the provided JWT token."
    )

    @GetMapping("/profile")
    public ResponseEntity<BasicUserProfileResponseDto> getUserProfile(@RequestHeader("Authorization") String token) {

        String jwtToken = token.substring(7);
        BasicUserProfileResponseDto userProfile = userService.getUserProfile(jwtToken);
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
