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
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
class AdsAndCategoriesControllerTest {

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
    private String authorizationAdmin;
    private String authorizationUser;
    private String authorizationUser2;
    private ObjectMapper om;
    User testUserRoleAdmin;
    User testUserRoleUser;
    User testUser2RoleUser;
    CategoryDTO testCategoryDTO;
    AdDTO testAdDTO;

    @BeforeAll
    public void setup() {

        UserRegisterDTO userRegisterDTO = new UserRegisterDTO("testUserRoleAdmin",
                "TestPassword1!", "testuser@testemail.test");

        testUserRoleAdmin = userService.registerUser(userRegisterDTO);
        testUserRoleAdmin.setRoles("ROLE_ADMIN");
        userService.saveUser(testUserRoleAdmin);
        userRegisterDTO = new UserRegisterDTO("testUserRoleUser",
                "TestPassword1!", "testuser2@testemail.test");
        testUserRoleUser = userService.registerUser(userRegisterDTO);
        userRegisterDTO = new UserRegisterDTO("testUser2RoleUser",
                "TestPassword1!", "testuser3@testemail.test");
        testUser2RoleUser = userService.registerUser(userRegisterDTO);
        authorizationAdmin = "Bearer ";
        authorizationAdmin += jwtService.generateToken("testUserRoleAdmin");
        authorizationUser = "Bearer ";
        authorizationUser += jwtService.generateToken("testUserRoleUser");
        authorizationUser2 = "Bearer ";
        authorizationUser2 += jwtService.generateToken("testUser2RoleUser");
        om = new ObjectMapper();

    }

    @AfterEach
    public void cleanUp() {
        adRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @BeforeEach
    public void BeforeEachTest() {
        testCategoryDTO = new CategoryDTO("testCategoryName",
                "testCategory description");
        testAdDTO = new AdDTO("Test Title",
                "Test description", 500.00, "12345", 1);
    }

    @Test
    void createCategoryWithValidData() throws Exception {

        testCategoryDTO.setName("createCategoryWithValidData");

        mockMvc.perform(post("/category")
                        .header("authorization", authorizationAdmin)
                        .content(om.writeValueAsString(testCategoryDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                        .value(testCategoryDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value(testCategoryDTO.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty());

    }


    @Test
    void createCategoryWithInvalidData() throws Exception {
        testCategoryDTO.setName("");

        mockMvc.perform(post("/category")
                        .header("authorization", authorizationAdmin)
                        .content(om.writeValueAsString(testCategoryDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Category name field can not be empty!"));
    }

    @Test
    void createCategoryWithAlreadyExistingData() throws Exception {

        categoryService.createCategory(testCategoryDTO);

        mockMvc.perform(post("/category")
                        .header("authorization", authorizationAdmin)
                        .content(om.writeValueAsString(testCategoryDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Category with this name already exists!"));
    }

    @Test
    void updateCategoryWithValidData() throws Exception {

        long id = categoryService.createCategory(testCategoryDTO).getId();
        testCategoryDTO.setName("testCategoryNameUpdated");

        mockMvc.perform(put("/category/" + id)
                        .header("authorization", authorizationAdmin)
                        .content(om.writeValueAsString(testCategoryDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                        .value(testCategoryDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value(testCategoryDTO.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id));
    }

    @Test
    void updateCategoryWithInvalidData() throws Exception {

        long id = categoryService.createCategory(testCategoryDTO).getId();
        testCategoryDTO.setName("");

        mockMvc.perform(put("/category/" + id)
                        .header("authorization", authorizationAdmin)
                        .content(om.writeValueAsString(testCategoryDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Category name field can not be empty!"));
    }

    @Test
    void updateCategoryWithValidData2() throws Exception {

        long id = categoryService.createCategory(testCategoryDTO).getId();
        testCategoryDTO.setDescription("testCategory description updated.");
        testCategoryDTO.setName("testCategoryNameFor_updateCategoryWithValidData2");

        mockMvc.perform(put("/category/" + id)
                        .header("authorization", authorizationAdmin)
                        .content(om.writeValueAsString(testCategoryDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                        .value(testCategoryDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value(testCategoryDTO.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id));
    }

    @Test
    void deleteCategoryWithValidData() throws Exception {

        long id = categoryService.createCategory(testCategoryDTO).getId();

        mockMvc.perform(delete("/category/" + id)
                        .header("authorization", authorizationAdmin)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Category has been deleted!"));
    }

    @Test
    void deleteCategoryWithInvalidData() throws Exception {

        long id = Long.MAX_VALUE;
        mockMvc.perform(delete("/category/" + id)
                        .header("authorization", authorizationAdmin)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Category doesn't exists!"));
    }

    @Test
    void creatAdWithValidData() throws Exception {

        long id = categoryService.createCategory(testCategoryDTO).getId();
        testAdDTO.setCategoryID(id);
        mockMvc.perform(post("/advertisement")
                        .header("authorization", authorizationUser)
                        .content(om.writeValueAsString(testAdDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                        .value(testAdDTO.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value(testAdDTO.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price")
                        .value(testAdDTO.getPrice()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipcode")
                        .value(testAdDTO.getZipcode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryID")
                        .value(id));
    }

    @Test
    void creatAdWithInvalidData() throws Exception {
        long id = categoryService.createCategory(testCategoryDTO).getId();
        testAdDTO.setCategoryID(id);
        testAdDTO.setTitle("");

        mockMvc.perform(post("/advertisement")
                        .header("authorization", authorizationUser)
                        .content(om.writeValueAsString(testAdDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Title field can not be empty!"));
    }

    @Test
    void creatAdWithToManyAdsForUser() throws Exception {

        Category category = categoryService.createCategory(testCategoryDTO);

        adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        testAdDTO.setTitle("Test Title 2");
        adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        testAdDTO.setTitle("Test Title 3");
        adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        testAdDTO.setTitle("Test Title 4");

        mockMvc.perform(post("/advertisement")
                        .header("authorization", authorizationUser)
                        .content(om.writeValueAsString(testAdDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("User is not allowed to create more ads."));
    }

    @Test
    void updateAdWithValidData() throws Exception {

        Category category = categoryService.createCategory(testCategoryDTO);
        long categoryID = category.getId();
        testAdDTO.setCategoryID(categoryID);
        Ad ad = adService.saveAd(new Ad(testAdDTO, testUserRoleUser,
                category));
        UUID id = ad.getId();
        testAdDTO.setTitle("Test Title 5");

        mockMvc.perform(put("/advertisement/" + id)
                        .header("authorization", authorizationUser)
                        .content(om.writeValueAsString(testAdDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                        .value(id.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                        .value(testAdDTO.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value(testAdDTO.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price")
                        .value(testAdDTO.getPrice()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipcode")
                        .value(testAdDTO.getZipcode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryID")
                        .value(categoryID));
    }

    @Test
    void updateAdWithInvalidData() throws Exception {

        Category category = categoryService.createCategory(testCategoryDTO);
        Ad ad = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        testAdDTO.setTitle("");
        UUID id = ad.getId();

        mockMvc.perform(put("/advertisement/" + id)
                        .header("authorization", authorizationUser)
                        .content(om.writeValueAsString(testAdDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Title field can not be empty!"));
    }

    @Test
    void updateAdWithInvalidUser() throws Exception {

        testCategoryDTO.setName("testCategoryNameFor_updateAdWithInvalidUser");
        Category category = categoryService.createCategory(testCategoryDTO);
        testAdDTO.setTitle("Test Title 6");
        Ad ad = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        UUID id = ad.getId();

        mockMvc.perform(put("/advertisement/" + id)
                        .header("authorization", authorizationUser2)
                        .content(om.writeValueAsString(testAdDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("User not authorized to update this ad!"));
    }

    @Test
    void deleteAdWithValidDataAndUser() throws Exception {

        Category category = categoryService.createCategory(testCategoryDTO);
        Ad ad = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        UUID id = ad.getId();

        mockMvc.perform(delete("/advertisement/" + id)
                        .header("authorization", authorizationUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAdWithValidDataAndUnauthorizedUser() throws Exception {

        Category category = categoryService.createCategory(testCategoryDTO);
        Ad ad = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        UUID id = ad.getId();

        mockMvc.perform(delete("/advertisement/" + id)
                        .header("authorization", authorizationUser2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")

                        .value("User not authorized to delete this ad!"));


    }

    @Test
    void deleteAdWithValidDataAndAdminUser() throws Exception {

        Category category = categoryService.createCategory(testCategoryDTO);
        Ad ad = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        UUID id = ad.getId();

        mockMvc.perform(delete("/advertisement/" + id)
                        .header("authorization", authorizationAdmin)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }


//    @Test
    void sendMessageToSellerWithValidData() throws Exception {

        testCategoryDTO.setName("testCategoryNameFor_sendMessageToSellerWithValidData");
        Category category = categoryService.createCategory(testCategoryDTO);
        testAdDTO.setTitle("Test Title 101");
        Ad ad = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        UUID id = ad.getId();

        String message = "Is the axe still available?.";
        MessageRequestDTO messageRequestDTO = new MessageRequestDTO(message);

        String url = "/advertisement/" + id + "/message";
        mockMvc.perform(post(url)
                        .header("Authorization", authorizationAdmin)
                        .content(om.writeValueAsString(messageRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value("200"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Thank you for your message."));

    }

    @Test
    void sendMessageToSelfShouldReturnBadRequest() throws Exception {

        testCategoryDTO.setName("testCategoryNameFor_sendMessageToSelfShouldReturnBadRequest");
        Category category = categoryService.createCategory(testCategoryDTO);
        testAdDTO.setTitle("Test Title 11");
        Ad ad = adService.saveAd(new Ad(testAdDTO, testUserRoleAdmin, category));
        UUID id = ad.getId();

        String message = "Is the axe still available?.";
        MessageRequestDTO messageRequestDTO = new MessageRequestDTO(message);

        String url = "/advertisement/" + id + "/message";
        System.out.println(url);

        mockMvc.perform(post(url)
                        .header("Authorization", authorizationAdmin)
                        .content(om.writeValueAsString(messageRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("You cannot send a message to yourself."));
    }

    @Test
    void getAdsWithValidId() throws Exception {
        Category category = categoryService.createCategory(testCategoryDTO);
        long categoryID = category.getId();
        testAdDTO.setCategoryID(categoryID);
        Ad ad = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        UUID id = ad.getId();

        mockMvc.perform(get("/advertisement/" + id)
                        .header("authorization", authorizationUser)
                        .content(om.writeValueAsString(testAdDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                        .value(id.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                        .value(testAdDTO.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value(testAdDTO.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price")
                        .value(testAdDTO.getPrice()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipcode")
                        .value(testAdDTO.getZipcode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryID")
                        .value(categoryID));
    }
    @Test
    void getAdsWithInvalidId() throws Exception {

        UUID invalidId = UUID.randomUUID();

        mockMvc.perform(get("/advertisement/" + invalidId)
                        .header("authorization", authorizationUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Ad not found!"));
    }

    @Test
    void getAdsWithValidUser() throws Exception {

        Category category = categoryService.createCategory(testCategoryDTO);
        Ad ad = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));

        List<AdResponseDTO> testAds = new ArrayList<>();
        testAds.add(new AdResponseDTO(ad));
        AdListResponseDTO testAdListResponseDTO = new AdListResponseDTO(testAds);

        mockMvc.perform(get("/advertisement")
                        .param("user", "testUserRoleUser")
                        .header("authorization", authorizationUser)
                        .content(om.writeValueAsString(testAdListResponseDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.Ads").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.Ads.length()").value(1));
    }
    @Test
    void getAdsWithInvalidUser() throws Exception {

        Category category = categoryService.createCategory(testCategoryDTO);
        Ad ad = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));

        List<AdResponseDTO> testAds = new ArrayList<>();
        testAds.add(new AdResponseDTO(ad));
        AdListResponseDTO testAdListResponseDTO = new AdListResponseDTO(testAds);

        mockMvc.perform(get("/advertisement")
                        .param("user", "testUserRoleUserError")
                        .header("authorization", authorizationUser)
                        .content(om.writeValueAsString(testAdListResponseDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("User not found!"));
    }
    @Test
    void getAdsWithValidCategoryId() throws Exception {

        Category category = categoryService.createCategory(testCategoryDTO);
        long categoryID = category.getId();
        testAdDTO.setTitle("Test Title 22");
        Ad ad = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        testAdDTO.setTitle("Test Title 23");
        Ad ad2 = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        testAdDTO.setTitle("Test Title 24");
        Ad ad3 = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));

        List<AdResponseDTO> testAds = new ArrayList<>();
        testAds.add(new AdResponseDTO(ad));
        testAds.add(new AdResponseDTO(ad2));
        testAds.add(new AdResponseDTO(ad3));

        Page<Ad> ads = adService.getAdsByCategoryAndPage(categoryID, 0);

        AdPageResponseDTO adPageResponseDTO = new AdPageResponseDTO(1, ads.getTotalPages(), testAds);

        mockMvc.perform(get("/advertisement")
                        .param("category", Long.toString(categoryID))
                        .header("authorization", authorizationUser)
                        .content(om.writeValueAsString(adPageResponseDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total_pages").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ads").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.ads.length()").value(2));
    }
    @Test
    void getAdsWithInvalidCategoryId() throws Exception {

        Category category = categoryService.createCategory(testCategoryDTO);
        long categoryID = category.getId();
        Ad ad = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));

        List<AdResponseDTO> testAds = new ArrayList<>();
        testAds.add(new AdResponseDTO(ad));

        String invalidCategoryId = Long.toString(categoryID+1);

        mockMvc.perform(get("/advertisement")
                        .param("category", invalidCategoryId)
                        .header("authorization", authorizationUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("No ads found for category: "  + invalidCategoryId));
    }


    @Test
    void getCategoryListWithoutParams() throws Exception {

        Category category = categoryService.createCategory(testCategoryDTO);
        long categoryID = category.getId();
        testCategoryDTO.setName("TestCategory2");
        Category category2 = categoryService.createCategory(testCategoryDTO);
        long categoryID2 = category.getId();
        testCategoryDTO.setName("TestCategory");

        testAdDTO.setTitle("Test Title 22");
        testAdDTO.setCategoryID(categoryID);
        Ad ad = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        testAdDTO.setTitle("Test Title 23");
        testAdDTO.setCategoryID(categoryID);
        Ad ad2 = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        testAdDTO.setTitle("Test Title 24");
        testAdDTO.setCategoryID(categoryID);
        Ad ad3 = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        category.setAds(List.of(ad, ad2, ad3));
        List<CategoryResponseCountDTO> listOfCategoryResponseCountDTO = new ArrayList<>();
        listOfCategoryResponseCountDTO.add(new CategoryResponseCountDTO(category, categoryService.adCountByCategory(category)));
        CategoryListResponseDTO response = new CategoryListResponseDTO();
        response.setCategories(listOfCategoryResponseCountDTO);

        mockMvc.perform(get("/category"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(om.writeValueAsString(response)));

    }

    @Test
    void getCategoryListWithParams() throws Exception {

        Category category = categoryService.createCategory(testCategoryDTO);
        long categoryID = category.getId();
        testCategoryDTO.setName("TestCategory2");
        Category category2 = categoryService.createCategory(testCategoryDTO);
        long categoryID2 = category.getId();
        testCategoryDTO.setName("TestCategory");

        testAdDTO.setTitle("Test Title 22");
        testAdDTO.setCategoryID(categoryID);
        Ad ad = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        testAdDTO.setTitle("Test Title 23");
        testAdDTO.setCategoryID(categoryID);
        Ad ad2 = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        testAdDTO.setTitle("Test Title 24");
        testAdDTO.setCategoryID(categoryID);
        Ad ad3 = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        category.setAds(List.of(ad, ad2, ad3));
        List<CategoryResponseCountDTO> listOfCategoryResponseCountDTO = new ArrayList<>();
        listOfCategoryResponseCountDTO.add(new CategoryResponseCountDTO(category, categoryService.adCountByCategory(category)));
        listOfCategoryResponseCountDTO.add(new CategoryResponseCountDTO(category2, categoryService.adCountByCategory(category2)));
        CategoryListResponseDTO response = new CategoryListResponseDTO();
        response.setCategories(listOfCategoryResponseCountDTO);

        mockMvc.perform(get("/category")
                        .param("empty", "true"))

                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(om.writeValueAsString(response)));
    }

    @Test
    void getCategoryListWithParams2() throws Exception {

        Category category = categoryService.createCategory(testCategoryDTO);
        long categoryID = category.getId();
        testCategoryDTO.setName("TestCategory2");
        Category category2 = categoryService.createCategory(testCategoryDTO);
        long categoryID2 = category.getId();
        testCategoryDTO.setName("TestCategory");

        testAdDTO.setTitle("Test Title 22");
        testAdDTO.setCategoryID(categoryID);
        Ad ad = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        testAdDTO.setTitle("Test Title 23");
        testAdDTO.setCategoryID(categoryID);
        Ad ad2 = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        testAdDTO.setTitle("Test Title 24");
        testAdDTO.setCategoryID(categoryID);
        Ad ad3 = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        category.setAds(List.of(ad, ad2, ad3));
        List<CategoryResponseCountDTO> listOfCategoryResponseCountDTO = new ArrayList<>();
        listOfCategoryResponseCountDTO.add(new CategoryResponseCountDTO(category, categoryService.adCountByCategory(category)));
        CategoryListResponseDTO response = new CategoryListResponseDTO();
        response.setCategories(listOfCategoryResponseCountDTO);

        mockMvc.perform(get("/category")
                        .param("empty", "false"))

                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(om.writeValueAsString(response)));
    }

    @Test
    void searchAdsByKeywordPositiveCase() throws Exception {

        Category category = categoryService.createCategory(testCategoryDTO);
        long categoryID = category.getId();
        testAdDTO.setTitle("KeywordPositiveCase");
        testAdDTO.setCategoryID(categoryID);
        Ad ad = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        testAdDTO.setTitle("Keyword PositiveCase2");
        testAdDTO.setCategoryID(categoryID);
        Ad ad2 = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        testAdDTO.setTitle("Keyword Positive Case3");
        testAdDTO.setCategoryID(categoryID);
        Ad ad3 = adService.saveAd(new Ad(testAdDTO, testUserRoleUser, category));
        List<AdResponseDTO> adResponseDTOS = new ArrayList<>();
        adResponseDTOS.add(new AdResponseDTO(ad));
        adResponseDTOS.add(new AdResponseDTO(ad2));
        adResponseDTOS.add(new AdResponseDTO(ad3));
        AdListResponseDTO response = new AdListResponseDTO(adResponseDTOS);
        mockMvc.perform(get("/advertisement")
                        .param("search", "positive"))

                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(om.writeValueAsString(response)));
    }

    @Test
    void searchAdsByKeywordNegativeCase() throws Exception {

        mockMvc.perform(get("/advertisement")
                        .param("search", "negative"))

                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("No ads found for the given keyword."));
    }
}
