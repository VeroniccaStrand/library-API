package com.pover.Library.controller;

import com.pover.Library.dto.ExtendedUserProfileRequestDto;
import com.pover.Library.dto.ExtendedUserProfileResponseDto;
import com.pover.Library.service.UserService;
import com.pover.Library.validation.UpdateValidationGroup;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

// ADMIN'S AND LIBRARIAN'S ACCESS TO USER'S PROFILE

@RestController
@RequestMapping("/api/admin/users/{memberNumber}/profile")
public class UserProfileAdminManagementController {

    private final UserService userService;

    public UserProfileAdminManagementController(UserService userService) {
        this.userService = userService;
    }

    //@PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @Operation(
            summary = "Get user profile by member number",
            description = "Retrieves the profile of a user by their member number. Accessible to ADMIN and LIBRARIAN roles."
    )
    @GetMapping
    public ResponseEntity<ExtendedUserProfileResponseDto> getUserProfileByMemberNumber(@PathVariable String memberNumber) {
        ExtendedUserProfileResponseDto userProfile = userService.getUserProfileByMemberNumber(memberNumber);
        if (userProfile == null) {
            throw new RuntimeException("UserProfile not found for memberNumber: " + memberNumber);
        }
        return ResponseEntity.ok(userProfile);
    }

    //@PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @Operation(
            summary = "Update user profile by member number",
            description = "Updates the profile of a user by their member number. Accessible to ADMIN and LIBRARIAN roles."
    )
    @PutMapping
    public ResponseEntity<ExtendedUserProfileResponseDto> updateUserProfileByMemberNumber(@PathVariable String memberNumber, @RequestBody @Validated(UpdateValidationGroup.class) ExtendedUserProfileRequestDto extendedUserProfileRequestDto) {
        extendedUserProfileRequestDto.setMember_number(memberNumber);
        ExtendedUserProfileResponseDto updatedProfile = userService.updateUserProfileByMemberNumber(extendedUserProfileRequestDto);
        return ResponseEntity.ok(updatedProfile);
    }
}
