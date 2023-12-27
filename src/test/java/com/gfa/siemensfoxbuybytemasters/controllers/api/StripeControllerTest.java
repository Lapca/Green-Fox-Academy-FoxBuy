package com.gfa.siemensfoxbuybytemasters.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.ChargeDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.UserRegisterDTO;
import com.gfa.siemensfoxbuybytemasters.models.User;
import com.gfa.siemensfoxbuybytemasters.services.JwtService;
import com.gfa.siemensfoxbuybytemasters.services.StripeService;
import com.gfa.siemensfoxbuybytemasters.services.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
class StripeControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    private String authorizationUser3;
    private ObjectMapper om;
    User testUser2RoleAdmin;
    User testUser3RoleUser;

    @BeforeAll
    public void setup() {

        UserRegisterDTO userRegisterDTO = new UserRegisterDTO("testUser2RoleAdmin",
                "TestPassword1!", "testuseradmin2@testemail.test");
        testUser2RoleAdmin = userService.registerUser(userRegisterDTO);
        userRegisterDTO = new UserRegisterDTO("testUser4RoleUser",
                "TestPassword1!", "testuser4@testemail.test");
        testUser3RoleUser = userService.registerUser(userRegisterDTO);
        authorizationUser3 = "Bearer ";
        authorizationUser3 += jwtService.generateToken("testUser4RoleUser");
        om = new ObjectMapper();

    }

    @Test
    void chargeWithValidPayment() throws Exception {

        ChargeDTO chargeDTO = new ChargeDTO("tok_visa", "huf", "vip upgrade",
                1, "Country", "Zcode", "City",
                "Address Line 1");
        mockMvc.perform(post("/vip")
                        .header("authorization", authorizationUser3)
                        .content(om.writeValueAsString(chargeDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("VIP upgrade is successful"));

    }

    @Test
    void chargeWithInvalidPayment() throws Exception {

        ChargeDTO chargeDTO = new ChargeDTO("tok_visaasd", "huf", "vip upgrade",
                1, "Country", "Zcode", "City",
                "Address Line 1");
        mockMvc.perform(post("/vip")
                        .header("authorization", authorizationUser3)
                        .content(om.writeValueAsString(chargeDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

}