package com.pover.Library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class BookRequestDto {

    @NotBlank(message = "Title cannot be blank")
    private String title;

//    @NotBlank(message = "Publication year cannot be blank")
//    @Pattern(
//            regexp = "^(19|20)\\d{2}$",
//            message = "Publication year must be a valid year in the format YYYY")
    @NotNull
    private int publication_year;

    private Long author_id;

    private Set<Long> genre_id;

    private boolean available;

}
