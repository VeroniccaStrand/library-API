package com.pover.Library.controller;

import com.pover.Library.dto.*;
import com.pover.Library.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.createUser(userRequestDto);
        return new ResponseEntity<>(userResponseDto, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all users in the system."
    )
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll() {
        List<UserResponseDto> users = userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves the details of a specific user based on the user ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable long id) {
        UserResponseDto userResponseDto = userService.getUserById(id);
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Login user",
            description = "Authenticates a user based on member number and password."
    )
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials) {
        String memberNumber = credentials.get("member_number");
        String password = credentials.get("password");

        if (memberNumber == null || password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing member number or password");
        }

        boolean isAuthenticated = userService.authenticateUser(memberNumber, password).isPresent();
        if (isAuthenticated) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @Operation(
            summary = "Get user profile",
            description = "Retrieves the profile of a user based on their member number."
    )
    @GetMapping("/profile/{memberNumber}")
    public ResponseEntity<ExtendedUserProfileResponseDto> getUserProfile(@PathVariable String memberNumber) {
        ExtendedUserProfileResponseDto userProfile = userService.getUserProfileByMemberNumber(memberNumber);
        return ResponseEntity.ok(userProfile);
    }

    @Operation(
            summary = "Update user profile",
            description = "Updates the profile of a user based on their member number."
    )
    @PutMapping("/profile/{memberNumber}")
    public ResponseEntity<BasicUserProfileResponseDto> updateUserProfile(
            @PathVariable String memberNumber,
            @RequestBody BasicUserProfileRequestDto basicUserProfileRequestDto) {
        BasicUserProfileResponseDto updatedProfile = userService.updateUserProfile(memberNumber, basicUserProfileRequestDto);
        return ResponseEntity.ok(updatedProfile);
    }

    @Operation(
            summary = "Logout user",
            description = "Logs out a user."
    )
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout successful");
    }
}
