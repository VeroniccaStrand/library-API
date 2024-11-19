package com.pover.Library.dto;

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
    private String member_number;
    private List<LoanResponseDto> activeLoans;

    public ExtendedUserProfileResponseDto(String first_name, String last_name, String email, String password, String member_number, List<LoanResponseDto> activeLoans) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.member_number = member_number;
        this.activeLoans = activeLoans;
    }
}
