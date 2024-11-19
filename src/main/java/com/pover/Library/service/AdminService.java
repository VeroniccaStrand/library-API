package com.pover.Library.service;

import com.pover.Library.dto.AdminRequestDto;
import com.pover.Library.dto.AdminResponseDto;
import com.pover.Library.model.Admin;
import com.pover.Library.repository.AdminRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import com.pover.Library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.pover.Library.model.User;


import java.util.List;
import java.util.stream.Collectors;


@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    private final AdminRepository adminRepository;
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public AdminResponseDto createAdmin(@Valid  AdminRequestDto adminRequestDto) {
        if(adminRepository.existsByUsername(adminRequestDto.getUsername())){
            throw new IllegalArgumentException("Username already exists");
        }
        Admin admin = new Admin();
        admin.setUsername(adminRequestDto.getUsername());
        admin.setPassword(adminRequestDto.getPassword());
        admin.setRole(adminRequestDto.getRole());
        adminRepository.save(admin);

        return new AdminResponseDto(admin.getAdmin_id(), admin.getUsername(), admin.getRole());
    }

    public List<AdminResponseDto> getAdmins() {
        return adminRepository.findAll()
                .stream()
                .map(admin -> new AdminResponseDto(admin.getAdmin_id(), admin.getUsername(), admin.getRole()))
                .collect(Collectors.toList());

    }

    public AdminResponseDto getAdminById(long id) {
        return adminRepository.findById(id)
                .map(admin -> new AdminResponseDto(admin.getAdmin_id(), admin.getUsername(),admin.getRole()))
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));
    }



    public Admin authenticate(@NotNull(message = "Username is required and unique") String username, @NotNull(message = "Password is required") String password) {
        Admin admin = adminRepository.findByUsername(username);
        if(admin != null && admin.getPassword().equals(password)){
            return admin;
        }
        return null;
    }
    public boolean logout(String token) {
        return token != null && !token.isEmpty();
    }

    public User getUserByMemberNumber(String memberNumber) {
        return userRepository.findByMemberNumber(memberNumber)
                .orElseThrow(() -> new RuntimeException("User not found with member number: " + memberNumber));
    }
}
