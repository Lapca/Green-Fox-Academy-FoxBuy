package com.gfa.siemensfoxbuybytemasters.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.LoginRequestDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.TokenDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.UserRegisterDTO;
import com.gfa.siemensfoxbuybytemasters.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {
//integration tests there shouldn't be any mocking

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    private ObjectMapper om;

    @BeforeEach
    public void BeforeEachTest() {
        om = new ObjectMapper();
    }

    @Test
    void registerUserWithValidData() throws Exception {
        Map<String, String> input = new HashMap<>();
        input.put("username", "newUser3");
        input.put("password", "veryNewPassword@1");
        input.put("email", "tbunukm@gmail.com");

        mockMvc.perform(post("/api/register")
                        .content(om.writeValueAsString(input))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void registerUserWithInvalidData() throws Exception {
        Map<String, String> input = new HashMap<>();
        input.put("username", "newUser2");
        input.put("password", "veryNewPassword");
        input.put("email", "tbunukm@gmail.com");

        mockMvc.perform(post("/api/register")
                        .content(om.writeValueAsString(input))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUserWithValidData() throws Exception {
        Map<String, String> input = new HashMap<>();
        input.put("username", "newUser2");
        input.put("password", "veryNewPassword1!");
        input.put("email", "email@gmail.com");

        mockMvc.perform(post("/api/register")
                .content(om.writeValueAsString(input))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/api/login")
                        .content(om.writeValueAsString(input))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void loginUserWithInvalidData() throws Exception {
        Map<String, String> input = new HashMap<>();
        input.put("username", "newUser2");
        input.put("password", "veryNewPassword1!");
        input.put("email", "email@gmail.com");

        Map<String, String> input2 = new HashMap<>();
        input.put("username", "newUser2");
        input.put("password", "veryNewPassword");

        mockMvc.perform(post("/api/register")
                .content(om.writeValueAsString(input))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/api/login")
                        .content(om.writeValueAsString(input2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void receivingAccessAndRefreshTokensAfterSuccessfulLogin() throws Exception {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO("newUser4",
                "Password1!", "email4@gmail.com");
        userService.registerUser(userRegisterDTO);
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("newUser4", "Password1!");

        mockMvc.perform(post("/api/login")
                        .content(om.writeValueAsString(loginRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refresh_token").isNotEmpty());
    }

    @Test
    void receivingAccessAndRefreshTokensFromRefreshEndpoint() throws Exception {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO("newUser5",
                "Password1!", "email5@gmail.com");
        userService.registerUser(userRegisterDTO);
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("newUser5", "Password1!");

        mockMvc.perform(post("/api/login")
                .content(om.writeValueAsString(loginRequestDTO))
                .contentType(MediaType.APPLICATION_JSON));
        TokenDTO tokenDTO = new TokenDTO("", userService.findByUsername("newUser5")
                .get().getRefreshToken());


        mockMvc.perform(post("/refresh")
                        .content(om.writeValueAsString(tokenDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refresh_token").isNotEmpty());
    }

    @Test
    void requestingTokensWithInvalidRefreshToken() throws Exception {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO("newUser6",
                "Password1!", "email6@gmail.com");
        userService.registerUser(userRegisterDTO);
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("newUser6", "Password1!");

        mockMvc.perform(post("/api/login")
                .content(om.writeValueAsString(loginRequestDTO))
                .contentType(MediaType.APPLICATION_JSON));
        TokenDTO tokenDTO = new TokenDTO("",
                "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuZXdVc2VyNiIsImlhdCI6MTY5NzExODIwOCwiZXhwIjoxN" +
                        "jk3MTE4Mzg4fQ.feyxw4uxlvgK-iUAidrXI6AnzQgGMJgXo3weHWuazps");


        mockMvc.perform(post("/refresh")
                        .content(om.writeValueAsString(tokenDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Refresh token has expired."));
    }

}
