package com.gfa.siemensfoxbuybytemasters.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfa.siemensfoxbuybytemasters.models.Ad;
import com.gfa.siemensfoxbuybytemasters.models.Category;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.*;
import com.gfa.siemensfoxbuybytemasters.models.User;
import com.gfa.siemensfoxbuybytemasters.repositories.AdRepository;
import com.gfa.siemensfoxbuybytemasters.repositories.CategoryRepository;
import com.gfa.siemensfoxbuybytemasters.services.AdService;
import com.gfa.siemensfoxbuybytemasters.services.CategoryService;
import com.gfa.siemensfoxbuybytemasters.services.JwtService;
import com.gfa.siemensfoxbuybytemasters.services.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerBanUserTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AdService adService;
    @Autowired
    private AdRepository adRepository;
    private String authorizationAdminForBan;
    private String authorizationUserForBan;
//    private String authorizationUser2ForBan;
    private ObjectMapper om;
    User testUserRoleAdminForBan;
    User testUserRoleUserForBan;
//    User testUser2RoleUserForBan;
    CategoryDTO testCategoryDTO;
    AdDTO testAdDTO;

    @BeforeAll
    public void setup() {

        UserRegisterDTO userRegisterDTO = new UserRegisterDTO("testUser11RoleAdmin",
                "TestPassword1!", "testuser11@testemail.test");

        testUserRoleAdminForBan = userService.registerUser(userRegisterDTO);
        testUserRoleAdminForBan.setRoles("ROLE_ADMIN");
        userService.saveUser(testUserRoleAdminForBan);
        userRegisterDTO = new UserRegisterDTO("testUser12RoleUser",
                "TestPassword1!", "testuser12@testemail.test");
        testUserRoleUserForBan = userService.registerUser(userRegisterDTO);
//        userRegisterDTO = new UserRegisterDTO("testUser13RoleUser",
//                "TestPassword1!", "testuser13@testemail.test");
//        testUser2RoleUserForBan = userService.registerUser(userRegisterDTO);
        authorizationAdminForBan = "Bearer ";
        authorizationAdminForBan += jwtService.generateToken("testUser11RoleAdmin");
        authorizationUserForBan = "Bearer ";
        authorizationUserForBan += jwtService.generateToken("testUser12RoleUser");
//        authorizationUser2ForBan = "Bearer ";
//        authorizationUser2ForBan += jwtService.generateToken("testUser13RoleUser");
        om = new ObjectMapper();

    }

    @AfterEach
    public void cleanUp() {
        adRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @BeforeEach
    public void BeforeEachTest() {
        testCategoryDTO = new CategoryDTO("testCategory11Name",
                "testCategory description");
        testAdDTO = new AdDTO("Test Title 91",
                "Test description", 500.00, "12345", 1);
    }

    @Test
    void banUserWithValidUuid() throws Exception {

        Category category = categoryService.createCategory(testCategoryDTO);
        long categoryID = category.getId();
        testAdDTO.setCategoryID(categoryID);
        adService.saveAd(new Ad(testAdDTO, testUserRoleUserForBan, category));
        testAdDTO.setCategoryID(categoryID);
        testAdDTO.setTitle("Test Title 31");
        adService.saveAd(new Ad(testAdDTO, testUserRoleUserForBan, category));
        testAdDTO.setCategoryID(categoryID);
        testAdDTO.setTitle("Test Title 32");
        Ad ad = adService.saveAd(new Ad(testAdDTO, testUserRoleUserForBan, category));

        UUID id = testUserRoleUserForBan.getId();
        BanRequestDTO banRequestDTO = new BanRequestDTO(1);

        mockMvc.perform(post("/user/" + id + "/ban")
                        .header("authorization", authorizationAdminForBan)
                        .content(om.writeValueAsString(banRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username")
                        .value(testUserRoleUserForBan.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.banned_until")
                        .isNotEmpty());

        UUID adID = ad.getId();

        mockMvc.perform(get("/advertisement/" + adID)
//                        .header("authorization", authorizationUser)
//                        .content(om.writeValueAsString(testAdDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Ad not found!"));
    }

    @Test
    void banUserWithInvalidUuid() throws Exception {

        String id = "4c86c012-e83d-4063-a3f4-364fa8ea67db";
        BanRequestDTO banRequestDTO = new BanRequestDTO(1);
        LocalDateTime localDateTime = LocalDateTime.now();

        mockMvc.perform(post("/user/" + id + "/ban")
                        .header("authorization", authorizationAdminForBan)
                        .content(om.writeValueAsString(banRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("User doesn't exist."));
    }

    @Test
    void loginBannedUser() throws Exception {
        UUID id = testUserRoleUserForBan.getId();
        BanRequestDTO banRequestDTO = new BanRequestDTO(1);

        mockMvc.perform(post("/user/" + id + "/ban")
                        .header("authorization", authorizationAdminForBan)
                        .content(om.writeValueAsString(banRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON));

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("testUser12RoleUser", "TestPassword1!");

        mockMvc.perform(post("/api/login")
                    .content(om.writeValueAsString(loginRequestDTO))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("The user is banned."));
    }

    @Test
    void createAdByBannedUser() throws Exception {
        UUID id = testUserRoleUserForBan.getId();
        BanRequestDTO banRequestDTO = new BanRequestDTO(1);

        mockMvc.perform(post("/user/" + id + "/ban")
                .header("authorization", authorizationAdminForBan)
                .content(om.writeValueAsString(banRequestDTO))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/advertisement")
                        .header("authorization", authorizationUserForBan)
                        .content(om.writeValueAsString(testAdDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
