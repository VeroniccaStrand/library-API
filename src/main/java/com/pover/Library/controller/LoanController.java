package com.pover.Library.controller;

import com.pover.Library.dto.LoanRequestDto;
import com.pover.Library.dto.LoanResponseDto;
import com.pover.Library.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/loans")
@Validated
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @Operation(
            summary = "Get all loans",
            description = "Retrieves a list of all loans in the system."
    )
    @GetMapping
    public ResponseEntity<List<LoanResponseDto>> getAllLoans() {
        List<LoanResponseDto> allLoans = loanService.getAllLoans();
        return ResponseEntity.ok(allLoans);
    }

    @Operation(
            summary = "Get loan by ID",
            description = "Retrieves the details of a specific loan based on the loan ID."
    )
    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponseDto> getLoanById(@PathVariable Long loanId) {
        LoanResponseDto loanResponseDto = loanService.getLoanById(loanId);
        return ResponseEntity.ok(loanResponseDto);
    }

    @Operation(
            summary = "Create a new loan",
            description = "Creates a new loan record in the system with the provided details."
    )
    @PostMapping
    public ResponseEntity<LoanResponseDto> createLoan(@Valid @RequestBody LoanRequestDto loanRequestDto) {
        LoanResponseDto loanResponseDto = loanService.createLoan(loanRequestDto);
        return ResponseEntity.status(201).body(loanResponseDto);
    }

    @Operation(
            summary = "Return a book",
            description = "Marks a loan as returned based on the loan ID."
    )
    @PutMapping("/return/{loanId}")
    @ResponseStatus(HttpStatus.OK)
    public LoanResponseDto returnBook(@PathVariable Long loanId) {
        return loanService.returnBook(loanId);
    }

    @Operation(
            summary = "Get active loans for a user",
            description = "Retrieves all active loans for a specific user based on their user ID."
    )
    @GetMapping("/{user_id}/active")
    public ResponseEntity<List<LoanResponseDto>> getUserActiveLoans(@PathVariable Long user_id) {
        List<LoanResponseDto> activeLoans = loanService.getUserActiveLoans(user_id);
        return ResponseEntity.ok(activeLoans);
    }
}
