package com.pover.Library.dto;

import com.pover.Library.model.Book;
import com.pover.Library.model.Loan;
import com.pover.Library.model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LoanResponseDto {

    private Long loan_id;
    private Book book;
    private LocalDate loan_date;
    private LocalDate due_date;
    private User user;

    public LoanResponseDto(Loan loan) {
        this.loan_id = loan.getLoan_id();
        this.book = loan.getBook();
        this.loan_date = loan.getLoan_date();
        this.due_date = loan.getDue_date();
        this.user = loan.getUser();
    }

}
