package com.pover.Library.dto;

import com.pover.Library.model.Book;
import com.pover.Library.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LoanRequestDto {

    @NotNull
    private Long bookId;

    @NotNull
    private Long userId;

    private LocalDate due_date;

    private LocalDate returned_date;

}
