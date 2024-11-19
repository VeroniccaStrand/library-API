package com.pover.Library.controller;

import com.pover.Library.JWT.JwtUtil;
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
    private final JwtUtil jwtUtil;

    public AdminController(AdminService adminService, JwtUtil jwtUtil){
        this.adminService = adminService;
        this.jwtUtil = jwtUtil;
    }



    @Operation(summary = "Create a new Admin", description = "Creates a new admin user with a unique username")
    @PostMapping("/create")
    public ResponseEntity<AdminResponseDto> create(@Valid @RequestBody AdminRequestDto adminRequestDto){
        AdminResponseDto adminResponseDto = adminService.createAdmin(adminRequestDto);

    //Service omvandlar admin till en responseDto
        return new ResponseEntity<>( adminResponseDto, HttpStatus.CREATED);
}
    @Operation(summary = "Get all Admins", description = "Get all admins incl librarians")
    @GetMapping("/get")
    public ResponseEntity<List<AdminResponseDto>> getAll(){
        List<AdminResponseDto> admins = adminService.getAdmins();
        return new ResponseEntity<>(admins, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<AdminResponseDto> getById(@PathVariable long id){
        AdminResponseDto adminResponseDto = adminService.getAdminById(id);
        return new ResponseEntity<>(adminResponseDto, HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<ResponseAdminLoginDto> login(@RequestBody AdminRequestDto adminRequestDto){
        Admin admin = adminService.authenticate(adminRequestDto.getUsername(), adminRequestDto.getPassword());

        if(admin == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String token = jwtUtil.generateToken(admin.getAdmin_id(),admin.getRole(), admin.getUsername(),null);

        ResponseAdminLoginDto responseAdminLoginDto = new ResponseAdminLoginDto(token);
        return new ResponseEntity<>(responseAdminLoginDto, HttpStatus.OK);
    }
    @Operation(summary = "Log out Admin", description = "Logs out the admin by invalidating the token")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Extrahera token
            boolean isLoggedOut = adminService.logout(token); // Anropar AdminService för att hantera logout

            if (isLoggedOut) {
                return new ResponseEntity<>("Successfully logged out", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Invalid logout request", HttpStatus.BAD_REQUEST);
    }

   // @DeleteMapping("/delete/{id}")
    //public ResponseEntity<AdminResponseDto> delete(@PathVariable long id, @AuthenticationPrincipal Admin currentAdmin){
      //  AdminResponseDto adminResponseDto = adminService.deleteAdminById(id, currentAdmin);
        //return new ResponseEntity<>(adminResponseDto, HttpStatus.OK);
    //}


}
