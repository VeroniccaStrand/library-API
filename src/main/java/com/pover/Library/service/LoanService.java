package com.pover.Library.service;

import com.pover.Library.dto.LoanRequestDto;
import com.pover.Library.dto.LoanResponseDto;
import com.pover.Library.model.Book;
import com.pover.Library.model.Loan;
import com.pover.Library.model.User;
import com.pover.Library.repository.LoanRepository;
import com.pover.Library.repository.BookRepository;
import com.pover.Library.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<LoanResponseDto> getAllLoans() {
        List<Loan> loans = loanRepository.findAll();
        return loans.stream()
                .map(LoanResponseDto::new)
                .collect(Collectors.toList());
    }


    public LoanResponseDto getLoanById(Long id) {
        Loan loan = loanRepository.findById(id).orElseThrow(()  -> new IllegalArgumentException("Loan not found"));
        return new LoanResponseDto(loan);
    }

    @Transactional
    public LoanResponseDto createLoan(LoanRequestDto loanRequestDto) {
        Book book = bookRepository.findById(loanRequestDto.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        User user = userRepository.findById(loanRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!book.isAvailable()) {
            throw new IllegalArgumentException("Book is not available for loan.");
        }

        book.setAvailable(false);
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoan_date(LocalDate.now());
        loan.setDue_date(LocalDate.now().plusWeeks(5));
        loan.setReturnedDate(null);
        loanRepository.save(loan);

        user.getLoans().add(loan);
        userRepository.save(user);


        return  new LoanResponseDto(loan);

    }

    @Transactional
    public LoanResponseDto returnBook(Long loanId) {

        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        loan.setReturnedDate(LocalDate.now());

        Book book = loan.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        User user = loan.getUser();
        if (user != null) {
            user.getLoans().remove(loan);
            userRepository.save(user);
        }

        loanRepository.save(loan);
        return new LoanResponseDto(loan);
    }


    public List<LoanResponseDto> getUserActiveLoans(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Loan> activeLoans = loanRepository.findByUserAndReturnedDateIsNull(user);


        return activeLoans.stream()
                .map(LoanResponseDto::new)
                .collect(Collectors.toList());
    }

}
