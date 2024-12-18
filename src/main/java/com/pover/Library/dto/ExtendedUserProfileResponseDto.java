package com.pover.Library.dto;

import com.pover.Library.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExtendedUserProfileResponseDto {

    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String personal_number;
    private String member_number;
    private List<LoanResponseDto> activeLoans;

    public ExtendedUserProfileResponseDto(User user, List<LoanResponseDto> activeLoans) {
        this.first_name = user.getFirst_name();
        this.last_name = user.getLast_name();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.personal_number = user.getPersonalNumber();
        this.member_number = user.getMemberNumber();
        this.activeLoans = activeLoans;
    }
}
