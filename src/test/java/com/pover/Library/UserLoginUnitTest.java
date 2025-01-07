package com.pover.Library;

import com.pover.Library.model.User;
import com.pover.Library.repository.UserRepository;
import com.pover.Library.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserLoginUnitTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void authenticateUser_shouldReturnToken() {
        User mockUser = new User();
        mockUser.setPersonalNumber("199004021009");
        mockUser.setPassword("SecUreP@SS12");

        Mockito.when(userRepository.findByPersonalNumber("199004021009")).thenReturn(Optional.of(mockUser));

        Optional<String> token = userService.authenticateUser("199004021009", "SecUreP@SS12");
        assertTrue(token.isPresent());
    }

    @Test
    public void authenticateUser_shouldFailForInvalidPassword() {

        User mockUser = new User();
        mockUser.setPersonalNumber("199004021009");
        mockUser.setPassword("SecUreP@SS12");

        Mockito.when(userRepository.findByPersonalNumber("199004021009")).thenReturn(Optional.of(mockUser));

        Optional<String> token = userService.authenticateUser("199001010000", "WrongP@ss");
        assertFalse(token.isPresent());
    }
}
