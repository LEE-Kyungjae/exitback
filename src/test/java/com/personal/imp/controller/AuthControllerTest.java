package com.personal.imp.controller;

import com.personal.imp.model.User;
import com.personal.imp.service.JwtTokenService;
import com.personal.imp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterNewUserSuccess() throws Exception {
        User newUser = new User();
        newUser.setEmail("newuser@example.com");
        when(userService.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"newuser@example.com\", \"password\": \"password\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Registration successful"));
    }

    @Test
    void testRegisterUserAlreadyExists() throws Exception {
        User existingUser = new User();
        existingUser.setEmail("existinguser@example.com");
        when(userService.findByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"existinguser@example.com\", \"password\": \"password\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Email already registered"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");
        when(authenticationManager.authenticate(any())).thenReturn(any());
        when(jwtTokenService.generateToken(anyString(), any())).thenReturn("mockToken");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"user@example.com\", \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("mockToken"));
    }

    @Test
    void testLoginBadCredentials() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"user@example.com\", \"password\": \"wrongPassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid email or password"));
    }

    @Test
    void testCheckEmailAvailable() throws Exception {
        when(userService.findByEmail("newuser@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/auth/check_email")
                        .param("email", "newuser@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"available\":true}"));
    }

    @Test
    void testCheckEmailUnavailable() throws Exception {
        User existingUser = new User();
        existingUser.setEmail("existinguser@example.com");
        when(userService.findByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/auth/check_email")
                        .param("email", "existinguser@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"available\":false}"));
    }
}
