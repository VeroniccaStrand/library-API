package com.pover.Library.service;

import com.pover.Library.JWT.JwtUtil;
import com.pover.Library.dto.AdminRequestDto;
import com.pover.Library.dto.AdminResponseDto;
import com.pover.Library.model.Admin;
import com.pover.Library.model.enums.Role;
import com.pover.Library.repository.AdminRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class AdminService {

    @Autowired
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AdminResponseDto createAdmin(@Valid  AdminRequestDto adminRequestDto) {
        if(adminRepository.existsByUsername(adminRequestDto.getUsername())){
            throw new IllegalArgumentException("Username already exists");
        }
        Admin admin = new Admin();
        admin.setUsername(adminRequestDto.getUsername());
        // admin.setPassword(adminRequestDto.getPassword());
        if (adminRequestDto.getRole() == null) {
            admin.setRole(Role.valueOf("LIBRARIAN"));
        } else {
            admin.setRole(adminRequestDto.getRole());
        }

        String encodedPassword = passwordEncoder.encode(adminRequestDto.getPassword());
        admin.setPassword(encodedPassword);

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


    public Optional<String> authenticate(String username, String password) {
        Optional<Admin> admin = adminRepository.findByUsername(username);

        if (admin.isPresent() && passwordEncoder.matches(password, admin.get().getPassword())){
            String token = jwtUtil.generateToken(admin.get().getAdmin_id(), admin.get().getRole(), admin.get().getUsername(), null);
            return Optional.of(token);
        }
        return Optional.empty();
    }
    public boolean logout(String token) {
        return token != null && !token.isEmpty();
    }

}
