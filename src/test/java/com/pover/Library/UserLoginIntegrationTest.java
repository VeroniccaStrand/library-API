package com.pover.Library;

import com.pover.Library.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setUp() {

        String token = "mocked-jwt-token";
        String personalNumber = "199004021009";
        String password = "SecureP@ss1";

        Mockito.when(userService.authenticateUser(personalNumber, password))
                .thenReturn(Optional.of(token));

//        Mockito.when(userService.authenticateUser("199004021009", "WrongPassword"))
//                .thenReturn(Optional.empty());
    }

    @Test
    public void login_shouldReturnToken() throws Exception {
        String requestBody = "{ \"personal_number\": \"199004021009\", \"password\": \"SecureP@ss1\" }";


        MvcResult result = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andReturn();

        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    public void login_shouldReturnBadRequestForMissingFields() throws Exception {
        String requestBody = "{ \"password\": \"SecureP@ss1\" }";

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Missing required fields: member number, password"));
    }
}
