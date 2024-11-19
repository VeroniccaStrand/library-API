package com.pover.Library.dto;

import com.pover.Library.validation.CreateValidationGroup;
import com.pover.Library.validation.UpdateValidationGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter

public class ExtendedUserProfileRequestDto {

    @NotBlank(groups = CreateValidationGroup.class)
    private String first_name;

    @NotBlank(groups = CreateValidationGroup.class)
    private String last_name;

    @NotBlank(groups = CreateValidationGroup.class)
    private String email;

    @Pattern(regexp = "^\\d{4}$", message = "Password must be 4 digits", groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    private String password;

    private String member_number;

}
