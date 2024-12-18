package com.pover.Library;

import com.pover.Library.dto.BasicUserProfileResponseDto;
import com.pover.Library.dto.ExtendedUserProfileResponseDto;
import com.pover.Library.dto.LoanResponseDto;
import com.pover.Library.model.Loan;
import com.pover.Library.model.User;
import com.pover.Library.repository.UserRepository;
import com.pover.Library.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetUserProfile_Success() {
        String personalNumber = "200203090245";

        User mockUser = new User();
        mockUser.setFirst_name("John");
        mockUser.setLast_name("Smith");
        mockUser.setEmail("smith@smith.com");
        mockUser.setMemberNumber(personalNumber);

        Loan activeLoan = new Loan();
        activeLoan.setLoan_date(LocalDate.now().minusDays(10));
        activeLoan.setDue_date(LocalDate.now().plusDays(20));
        activeLoan.setReturnedDate(null);
        mockUser.setLoans(List.of(activeLoan));

        when(userRepository.findByPersonalNumber(personalNumber)).thenReturn(Optional.of(mockUser));

        ExtendedUserProfileResponseDto response = userService.getUserProfileByPersonalNumber(personalNumber);

        assertNotNull(response);
        assertEquals("John", response.getFirst_name());
        assertEquals("Smith", response.getLast_name());
        assertEquals("smith@smith.com", response.getEmail());
        assertNotNull(response.getActiveLoans());
        assertEquals(1, response.getActiveLoans().size());

        LoanResponseDto loanResponse = response.getActiveLoans().get(0);
        assertEquals(activeLoan.getLoan_date(), loanResponse.getLoan_date());
        assertEquals(activeLoan.getDue_date(), loanResponse.getDue_date());

        verify(userRepository, times(1)).findByPersonalNumber(personalNumber);
    }
}
