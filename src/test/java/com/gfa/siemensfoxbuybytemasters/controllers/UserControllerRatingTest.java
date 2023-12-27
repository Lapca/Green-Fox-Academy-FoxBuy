package com.gfa.siemensfoxbuybytemasters.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.RatingReactionDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.RatingRequestDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.UserRegisterDTO;
import com.gfa.siemensfoxbuybytemasters.models.Rating;
import com.gfa.siemensfoxbuybytemasters.models.User;
import com.gfa.siemensfoxbuybytemasters.repositories.RatingRepository;
import com.gfa.siemensfoxbuybytemasters.services.JwtService;
import com.gfa.siemensfoxbuybytemasters.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerRatingTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RatingRepository ratingRepository;
    private String authorizationAdminForRating;
    private String authorizationUserForRating;
    private String authorizationUser2ForRating;
    private ObjectMapper om;
    User testUserRoleAdminForRating;
    User testUserRoleUserForRating;
    User testUser2RoleUserForRating;
    RatingRequestDTO testRatingRequestDTO;

    @BeforeAll
    public void setup() {

        UserRegisterDTO userRegisterDTO = new UserRegisterDTO("testUser21RoleAdmin",
                "TestPassword1!", "testuser21@testemail.test");

        testUserRoleAdminForRating = userService.registerUser(userRegisterDTO);
        testUserRoleAdminForRating.setRoles("ROLE_ADMIN");
        userService.saveUser(testUserRoleAdminForRating);
        userRegisterDTO = new UserRegisterDTO("testUser22RoleUser",
                "TestPassword1!", "testuser22@testemail.test");
        testUserRoleUserForRating = userService.registerUser(userRegisterDTO);
        userRegisterDTO = new UserRegisterDTO("testUser23RoleUser",
                "TestPassword1!", "testuser23@testemail.test");
        testUser2RoleUserForRating = userService.registerUser(userRegisterDTO);
        authorizationAdminForRating = "Bearer ";
        authorizationAdminForRating += jwtService.generateToken("testUser21RoleAdmin");
        authorizationUserForRating = "Bearer ";
        authorizationUserForRating += jwtService.generateToken("testUser22RoleUser");
        authorizationUser2ForRating = "Bearer ";
        authorizationUser2ForRating += jwtService.generateToken("testUser23RoleUser");
        om = new ObjectMapper();

    }
    @AfterEach
    public void cleanUp() {
        ratingRepository.deleteAll();
    }

    @BeforeEach
    public void BeforeEachTest() {
        testRatingRequestDTO = new RatingRequestDTO(5,
                "test rating description");
    }

    @Test
    void createRatingWithValidData() throws Exception {

        UUID id = testUserRoleUserForRating.getId();

        mockMvc.perform(post("/user/" + id + "/rating")
                        .header("authorization", authorizationUser2ForRating)
                        .content(om.writeValueAsString(testRatingRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.Id")
                        .isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.Rating")
                        .value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.Comment")
                        .value("test rating description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.Reaction")
                        .value("")
                );
    }

    @Test
    void createRatingWithInvalidUser() throws Exception {

        UUID id = testUserRoleUserForRating.getId();

        mockMvc.perform(post("/user/" + id + "/rating")
                        .header("authorization", authorizationUserForRating)
                        .content(om.writeValueAsString(testRatingRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("You cannot rate yourself!")
                );
    }

    @Test
    void setReactionForRatingWithValidData() throws Exception {

        UUID id = testUserRoleUserForRating.getId();

        mockMvc.perform(post("/user/" + id + "/rating")
                .header("authorization", authorizationUser2ForRating)
                .content(om.writeValueAsString(testRatingRequestDTO))
                .contentType(MediaType.APPLICATION_JSON));

        Rating rating = ratingRepository.findFirstByOrderByIdDesc();
        long ratingId = rating.getId();
        RatingReactionDTO ratingReactionDTO = new RatingReactionDTO("Thank you for your rating.");

        mockMvc.perform(post("/user/rating/" + ratingId)
                .header("authorization", authorizationUserForRating)
                .content(om.writeValueAsString(ratingReactionDTO))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }

    @Test
    void setReactionForRatingWithInvalidData() throws Exception {

        UUID id = testUserRoleUserForRating.getId();

        mockMvc.perform(post("/user/" + id + "/rating")
                .header("authorization", authorizationUser2ForRating)
                .content(om.writeValueAsString(testRatingRequestDTO))
                .contentType(MediaType.APPLICATION_JSON));

        Rating rating = ratingRepository.findFirstByOrderByIdDesc();
        long ratingId = rating.getId();
        RatingReactionDTO ratingReactionDTO = new RatingReactionDTO("");

        mockMvc.perform(post("/user/rating/" + ratingId)
                        .header("authorization", authorizationUserForRating)
                        .content(om.writeValueAsString(ratingReactionDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("error")
                        .value("Reaction is missing!"));
    }
    @Test
    void listRatingsWithValidData() throws Exception {

        UUID id = testUserRoleUserForRating.getId();

        mockMvc.perform(post("/user/" + id + "/rating")
                .header("authorization", authorizationUser2ForRating)
                .content(om.writeValueAsString(testRatingRequestDTO))
                .contentType(MediaType.APPLICATION_JSON));

        Rating rating = ratingRepository.findFirstByOrderByIdDesc();
        long ratingId = rating.getId();
        String ratingID = String.valueOf(ratingId);

        mockMvc.perform(get("/user/" + id + "/rating")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.Average")
                        .value(5.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.Ratings", hasSize(1)));
        }

    @Test
    void listRatingsWithInvalidUUID() throws Exception {

        String id = "4c86c012-e83d-4063-a3f4-364fa8ea67db";

        mockMvc.perform(get("/user/" + id + "/rating")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("User doesn't exist."));
    }

    @Test
    void deleteRatingWithValidData() throws Exception {

        UUID id = testUserRoleUserForRating.getId();

        mockMvc.perform(post("/user/" + id + "/rating")
                .header("authorization", authorizationUser2ForRating)
                .content(om.writeValueAsString(testRatingRequestDTO))
                .contentType(MediaType.APPLICATION_JSON));

        Rating rating = ratingRepository.findFirstByOrderByIdDesc();
        long ratingId = rating.getId();

        mockMvc.perform(delete("/user/" + id + "/rating/" + ratingId)
                    .header("authorization", authorizationUser2ForRating)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.Id")
                        .value(ratingId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.Rating")
                        .value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.Comment")
                        .value("test rating description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.Reaction")
                        .value(""));

    }

    @Test
    void deleteRatingWithInvalidData() throws Exception {

        UUID id = testUserRoleUserForRating.getId();

        mockMvc.perform(post("/user/" + id + "/rating")
                .header("authorization", authorizationUser2ForRating)
                .content(om.writeValueAsString(testRatingRequestDTO))
                .contentType(MediaType.APPLICATION_JSON));

        Rating rating = ratingRepository.findFirstByOrderByIdDesc();
        long ratingId = rating.getId();

        mockMvc.perform(delete("/user/" + id + "/rating/" + ratingId)
                        .header("authorization", authorizationUserForRating)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("You cannot delete other's ratings on yourself!"));

    }
}
