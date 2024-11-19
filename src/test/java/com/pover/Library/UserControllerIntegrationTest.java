package com.pover.Library;

import com.pover.Library.model.User;
import com.pover.Library.model.enums.Role;
import com.pover.Library.repository.UserRepository;
import com.pover.Library.JWT.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setFirst_name("John");
        user.setLast_name("Smith");
        user.setEmail("smith@smith.com");
        user.setMemberNumber("200203090245");
        user.setPassword("1234");
        user.setRole(Role.USER);
        userRepository.save(user);

        jwtToken = jwtUtil.generateToken(user.getUser_id(), user.getRole(), null, user.getMemberNumber());
    }

    @Test
    void testGetUserProfile_Success() throws Exception {
        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name", is("John")))
                .andExpect(jsonPath("$.last_name", is("Smith")))
                .andExpect(jsonPath("$.email", is("smith@smith.com")))
                .andExpect(jsonPath("$.activeLoans", hasSize(0)));
    }
}
