package com.pover.Library.controller;

import com.pover.Library.dto.AdminRequestDto;
import com.pover.Library.dto.AdminResponseDto;
import com.pover.Library.dto.ResponseAdminLoginDto;
import com.pover.Library.model.Admin;
import com.pover.Library.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "Create a new Admin", description = "Creates a new admin user with a unique username")
    @PostMapping("/create")
    public ResponseEntity<AdminResponseDto> create(@Valid @RequestBody AdminRequestDto adminRequestDto) {
        AdminResponseDto adminResponseDto = adminService.createAdmin(adminRequestDto);
        return new ResponseEntity<>(adminResponseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all Admins", description = "Get all admins incl librarians")
    @GetMapping("/get")
    public ResponseEntity<List<AdminResponseDto>> getAll() {
        List<AdminResponseDto> admins = adminService.getAdmins();
        return new ResponseEntity<>(admins, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<AdminResponseDto> getById(@PathVariable long id) {
        AdminResponseDto adminResponseDto = adminService.getAdminById(id);
        return new ResponseEntity<>(adminResponseDto, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AdminRequestDto adminRequestDto) {
        Admin admin = adminService.authenticate(adminRequestDto.getUsername(), adminRequestDto.getPassword());

        if (admin == null) {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>("Login successful", HttpStatus.OK);
    }

    @Operation(summary = "Log out Admin", description = "Logs out the admin")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return new ResponseEntity<>("Logout successful", HttpStatus.OK);
    }
}
