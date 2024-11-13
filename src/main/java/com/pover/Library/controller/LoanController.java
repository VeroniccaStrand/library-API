package com.pover.Library.controller;

import com.pover.Library.dto.LoanRequestDto;
import com.pover.Library.dto.LoanResponseDto;
import com.pover.Library.service.LoanService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/loans")
@Validated
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponseDto> getLoanById(@PathVariable Long loanId) {
        LoanResponseDto loanResponseDto = loanService.getLoanById(loanId);
        return ResponseEntity.ok(loanResponseDto);
    }

    @PostMapping
    public ResponseEntity<LoanResponseDto> createLoan(@Valid @RequestBody LoanRequestDto loanRequestDto) {
        LoanResponseDto loanResponseDto = loanService.createLoan(loanRequestDto);
        return ResponseEntity.status(200).body(loanResponseDto);
    }

    @PutMapping("/update-due-date/{loanId}")
    public ResponseEntity<LoanResponseDto> updateLoanDueDate(@PathVariable Long loanId, @RequestParam @NotNull LocalDate newDueDate) {
        LoanResponseDto loanResponseDto = loanService.updateLoanDueDate(loanId, newDueDate);
        return ResponseEntity.ok(loanResponseDto);
    }

    @GetMapping("/user/{user_id}/active")
    public ResponseEntity<List<LoanResponseDto>> getUserActiveLoans(@PathVariable Long user_id) {
        List<LoanResponseDto> activeLoans = loanService.getUserActiveLoans(user_id);
        return ResponseEntity.ok(activeLoans);
    }
}