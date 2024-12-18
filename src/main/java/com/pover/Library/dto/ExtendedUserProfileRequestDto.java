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

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters with upper/lowercase letters, a number, and a special character."
    )
    private String password;

    @NotBlank(message = "Personal number is required")
    @Pattern(
            regexp = "^(19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])\\d{4}$",
            message = "Please enter twelve digits"
    )
    private String personal_number;


    private String member_number;

}
