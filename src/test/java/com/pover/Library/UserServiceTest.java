package com.pover.Library;

import com.pover.Library.JWT.JwtUtil;
import com.pover.Library.dto.BasicUserProfileResponseDto;
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

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetUserProfile_Success(){
        String token = "valid.jwt.token";
        String memberNumber = "200203090245";

        when(jwtUtil.extractMemberNumber(token)).thenReturn(memberNumber);

        User mockUser = new User();
        mockUser.setFirst_name("John");
        mockUser.setLast_name("Smith");
        mockUser.setEmail("smith@smith.com");
        mockUser.setMemberNumber(memberNumber);

        Loan activeLoan = new Loan();
        activeLoan.setLoan_date(LocalDate.now().minusDays(10));
        activeLoan.setDue_date(LocalDate.now().plusDays(20));
        activeLoan.setReturnedDate(null);
        mockUser.setLoans(List.of(activeLoan));


        when(jwtUtil.extractMemberNumber(token)).thenReturn(memberNumber);

        when(userRepository.findByMemberNumber(memberNumber)).thenReturn(Optional.of(mockUser));

        BasicUserProfileResponseDto response = userService.getUserProfile(token);

        assertNotNull(response);
        assertEquals("John", response.getFirst_name());
        assertEquals("Smith", response.getLast_name());
        assertEquals("smith@smith.com", response.getEmail());
        assertNotNull(response.getActiveLoans());
        assertEquals(1, response.getActiveLoans().size());
        LoanResponseDto loanResponse = response.getActiveLoans().get(0);
        assertEquals(activeLoan.getLoan_date(), loanResponse.getLoan_date());
        assertEquals(activeLoan.getDue_date(), loanResponse.getDue_date());

        verify(jwtUtil, times(1)).extractMemberNumber(token);
        verify(userRepository, times(1)).findByMemberNumber(memberNumber);


    }
}
