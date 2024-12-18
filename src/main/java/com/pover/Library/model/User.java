package com.pover.Library.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pover.Library.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "member_number")})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long user_id;

    @NotNull
    private String first_name;
    @NotNull
    private String last_name;

    @NotNull
    @Email(message = "Email must be valid")
    private String email;

    @NotNull
    @Column(name = "member_number", unique = true)
    private String memberNumber;

    @NotNull
    @Column(name = "personal_number", unique = true)
    private String personalNumber;

    @NotNull
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Loan> loans;
}
