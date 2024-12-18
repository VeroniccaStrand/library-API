package com.pover.Library.dto;

import com.pover.Library.model.User;
import com.pover.Library.model.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {

    private Long user_id;
    private String first_name;
    private String last_name;
    private String member_number;
    private String personal_number;
    private Role role;

    public UserResponseDto(User user) {
        this.user_id = user.getUser_id();
        this.first_name = user.getFirst_name();
        this.last_name = user.getLast_name();
        this.member_number = user.getMemberNumber();
        this.personal_number = user.getPersonalNumber();
        this.role = Role.USER;

    }

}
