package com.gfa.siemensfoxbuybytemasters.controllers;

import com.gfa.siemensfoxbuybytemasters.models.*;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.*;
import com.gfa.siemensfoxbuybytemasters.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class AdsAndCategoriesController {

    private final CategoryService categoryService;
    private final UserService userService;
    private final AdService adService;
    private final EmailService emailService;
    private final LogService logService;
    private final WatchdogService watchdogService;
    private final StripeService stripeService;

    @Autowired
    public AdsAndCategoriesController(CategoryService categoryService, UserService userService,
                                      AdService adService, EmailService emailService, LogService logService,
                                      WatchdogService watchdogService, StripeService stripeService) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.adService = adService;
        this.emailService = emailService;
        this.logService = logService;
        this.watchdogService = watchdogService;
        this.stripeService = stripeService;
    }

    @Operation(
            summary = "Creates an ad category",
            description = "Creates a new ad category."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Category created successfully."),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input or category with the same name already exists.")
    })
    @Parameter(name = "categoryDTO", description = "Category data to create")
    @PostMapping("/category")
    public ResponseEntity<?> createCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            logService.logRequest("/category", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(categoryService.buildErrorJsonResponseforCategory(bindingResult));
        }

        if (categoryService.existsByName(categoryDTO.getName())) {
            logService.logRequest("/category", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorDTO("Category with this name already exists!"));
        }

        Category category = categoryService.createCategory(categoryDTO);
        CategoryResponseDTO response = new CategoryResponseDTO(category);
        logService.logRequest("/category", true);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Updates an ad category",
            description = "Updates an existing ad category."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Category updated successfully."),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input or category with the given ID does not exist.")
    })
    @Parameter(name = "id", description = "Category ID to update")
    @Parameter(name = "categoryDTO", description = "Category data with changes")
    @PutMapping("/category/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable long id,
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            logService.logRequest("/category/{id}", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(categoryService.buildErrorJsonResponseforCategory(bindingResult));
        }

        Optional<Category> optionalCategory = categoryService.findCategoryById(id);
        if (optionalCategory.isPresent()) {
            logService.logRequest("/category/{id}", true);

            Category category = optionalCategory.get();
            category.setName(categoryDTO.getName());
            category.setDescription(categoryDTO.getDescription());

            CategoryResponseDTO response =
                    new CategoryResponseDTO(categoryService.createCategory(category));

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            logService.logRequest("/category/{id}", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorDTO("No category exists with the given ID."));
        }
    }

    @Operation(
            summary = "Deletes an ad category",
            description = "Deletes an existing ad category by ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Category deleted successfully."),
            @ApiResponse(responseCode = "400",
                    description = "Invalid category ID or category deletion failed.")
    })
    @DeleteMapping("/category/{id}")
    public ResponseEntity<?> deleteCategory(
            @Parameter(description = "Category ID to delete") @PathVariable long id) {

        return categoryService.deleteCategory(id);
    }


    @Operation(
            summary = "Creates a new advertisement," +
                    " there is opportunity to also create a digital product.",
            description = "Creates a new advertisement and associates it with the current user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Advertisement created successfully."),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input, user does not exist," +
                            " or user is not allowed to create more ads, " +
                            " or only VIP users can create digital products.")
    })
    @Parameter(name = "adDTO", description = "Advertisement data to create")


    @PostMapping("/advertisement")
    public ResponseEntity<?> createAd(
            @Valid @RequestBody AdDTO adDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            logService.logRequest("/advertisement", false);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(adService.buildErrorJsonResponseforAd(bindingResult));
        }

        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        Category category = categoryService.findCategoryById(adDTO.getCategoryID())
                .orElse(null);

        if (optionalUser.isEmpty()) {
            logService.logRequest("/advertisement", false);

            return ResponseEntity.badRequest().body(new ErrorDTO("User does not exist."));
        }

        User user = optionalUser.get();

        if(adDTO.getProductKey() != null && !Objects.equals(user.getRoles(), "ROLE_VIP")) {
            logService.logRequest("/advertisement", false);

            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("Only VIP users can create digital products!"));
        }

        if (adService.allowedToCreateMoreAd(user) && category != null) {

            if(adDTO.getProductKey() != null) {
                String productKey = adDTO.getProductKey();
                DigitalProduct digitalProduct = new DigitalProduct(adDTO, user, category, productKey);

                logService.logRequest("/advertisement", true);

                return ResponseEntity.ok()
                        .body(new DigitalProductDTO(adService.saveDigitalProduct(digitalProduct)));
            }

            Ad ad = new Ad(adDTO, user, category);

            watchdogService.checkWatchdogs(adDTO);

            logService.logRequest("/advertisement", true);
            return ResponseEntity.ok().body(new AdResponseDTO(adService.saveAd(ad)));
        }
        logService.logRequest("/advertisement", false);
        return ResponseEntity.badRequest()
                .body(new ErrorDTO("User is not allowed to create more ads."));
    }

    @Operation(
            summary = "Updates an advertisement",
            description = "Updates an existing advertisement associated with the current user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Advertisement updated successfully."),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input, user not authorized," +
                            " advertisement does not exist, or category does not exist.")
    })
    @Parameter(name = "adDTO", description = "Advertisement data to update")
    @Parameter(name = "id", description = "Advertisement ID to update")
    @PutMapping("/advertisement/{id}")
    public ResponseEntity<?> updateAd(
            @Valid @RequestBody AdDTO adDTO,
            BindingResult bindingResult,
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            logService.logRequest("/advertisement/{id}", false);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(adService.buildErrorJsonResponseforAd(bindingResult));
        }

        User user = userService.findByUsername(userDetails.getUsername()).orElse(null);
        Ad ad = adService.findAdById(id).orElse(null);

        if (ad != null && user != null && ad.getUser() == user) {
            Category category = categoryService.findCategoryById(adDTO.getCategoryID())
                    .orElse(null);
            if (category == null) {
                logService.logRequest("/advertisement/{id}", false);

                return ResponseEntity.badRequest().body(new ErrorDTO("Category does not exist."));
            }
            ad.setCategory(category);
            ad.setTitle(adDTO.getTitle());
            ad.setDescription(adDTO.getDescription());
            ad.setPrice(adDTO.getPrice());
            ad.setZipcode(adDTO.getZipcode());
            adService.saveAd(ad);

            watchdogService.checkWatchdogs(adDTO);

            logService.logRequest("/advertisement/{id}", true);
            return ResponseEntity.ok().body(new AdResponseDTO(ad));
        } else {
            if (ad != null && ad.getUser() != user) {
                logService.logRequest("/advertisement/{id}", false);

                return ResponseEntity.badRequest().body(new ErrorDTO("User not authorized to update this ad!"));

            }
            logService.logRequest("/advertisement/{id}", false);

            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("Advertisement with the given ID does not exist."));
        }
    }

    @Operation(
            summary = "Deletes an advertisement",
            description = "Deletes an existing advertisement associated with the current user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Advertisement deleted successfully."),
            @ApiResponse(responseCode = "400",
                    description = "User does not exist, advertisement does not exist," +
                            " or user is not authorized to delete the ad.")
    })
    @Parameter(name = "id", description = "Advertisement ID to delete")
    @DeleteMapping("/advertisement/{id}")
    public ResponseEntity<?> deleteAd(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByUsername(userDetails.getUsername()).orElse(null);

        if (user == null) {
            logService.logRequest("/advertisement/{id}", false);

            return ResponseEntity.badRequest().body(new ErrorDTO("User does not exist."));
        }

        Ad ad = adService.findAdById(id).orElse(null);

        if (ad == null) {
            logService.logRequest("/advertisement/{id}", false);

            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("Advertisement with the given ID does not exist."));
        }

        if (ad.getUser() == user || user.getRoles().equals("ROLE_ADMIN")) {
            adService.deleteAd(ad);
            logService.logRequest("/advertisement/{id}", true);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            logService.logRequest("/advertisement/{id}", false);

            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("User not authorized to delete this ad!"));
        }
    }

    @Operation(
            summary = "Send Message to Seller",
            description = "Send a message to the seller of a specific advertisement." +
                    " Only logged-in users can send messages," +
                    " and a user cannot send a message to themselves."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Message sent successfully to the seller."),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input, user not authorized," +
                            " attempting to send a message to oneself," +
                            " or advertisement does not exist.")
    })
    @Parameter(name = "id",
            description = "Advertisement ID to send a message to the seller",
            required = true)
    @Parameter(name = "messageRequest", description = "Message request body", required = true)
    @PostMapping("/advertisement/{id}/message")
    public ResponseEntity<?> sendMessageToSeller(
            @PathVariable("id") UUID adId,
            @RequestBody MessageRequestDTO messageRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            logService.logRequest("/advertisement/{id}/message", false);

            return ResponseEntity
                    .badRequest()
                    .body(new ErrorDTO("Only logged-in users can send messages!"));
        }
        User user = optionalUser.get();

        Optional<Ad> optionalAd = adService.findAdById(adId);
        if (optionalAd.isEmpty()) {
            logService.logRequest("/advertisement/{id}/message", false);

            return ResponseEntity
                    .badRequest()
                    .body(new ErrorDTO("No ad found - invalid ID?"));
        }
        Ad ad = optionalAd.get();

        // Check if the user is trying to send a message to themselves
        if (user.equals(ad.getUser())) {
            logService.logRequest("/advertisement/{id}/message", false);

            return ResponseEntity
                    .badRequest()
                    .body(new ErrorDTO("You cannot send a message to yourself."));
        }

        String subject = ad.getTitle();
        String email = ad.getUser().getEmail();
        List<String> recipients = List.of(email);
        String body = messageRequest.getMessage() + user.getEmail();

        try {
            emailService.sendHtmlEmail(recipients, subject, body);
        } catch (MessagingException e) {
            logService.logRequest("/advertisement/{id}/message", false);

            return ResponseEntity
                    .badRequest()
                    .body(new ErrorDTO("Messaging exception thrown!"));
        }
        logService.logRequest("/advertisement/{id}/message", true);

        return ResponseEntity
                .ok(new MessageResponseDTO("200", "Thank you for your message."));
    }

    @GetMapping("/advertisement/{id}")
    public ResponseEntity<?> getAdById(@PathVariable UUID id) {
        Optional<Ad> optionalAd = adService.findAdById(id);

        if (optionalAd.isPresent()) {
            logService.logRequest("/advertisement/{id}", true);

            return ResponseEntity.ok().body(new AdResponseDTO(optionalAd.get()));
        } else {
            logService.logRequest("/advertisement/{id}", false);

        return ResponseEntity.badRequest().body(new ErrorDTO("Ad not found!"));}
    }
    @GetMapping("/advertisement")
    public ResponseEntity<?> getAds(
            @RequestParam(required = false) String user,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1", required = false) int page) {

        if (search != null && !search.trim().isEmpty()) {
            List<Ad> foundAds = adService.searchAdsByKeyword(search);
            if (foundAds.isEmpty()) {
                logService.logRequest("/advertisement?search", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("No ads found for the given keyword."));
            }

            List<AdResponseDTO> responseAds = foundAds.stream()
                    .map(AdResponseDTO::new)
                    .collect(Collectors.toList());

            logService.logRequest("/advertisement?search", true);
            return ResponseEntity.ok(new AdListResponseDTO(responseAds));
        } else if (user != null) {
            return adService.getAdsByUser(user);
        } else if (category != null) {
            long categoryId = category.longValue();
            return adService.getAdsByCategory(categoryId, page);
        } else {
            logService.logRequest("/advertisement", false);
            return ResponseEntity.badRequest().body(new ErrorDTO("Invalid parameters"));
        }
    }

    @GetMapping("/category")
    public ResponseEntity<?> listCategoriesWithEmpty(
            @RequestParam(value = "empty", defaultValue = "false") boolean empty) {
        List<Category> categories = categoryService.getAllCategories();
        for (Category category:categories) {
            category.setAdCount(categoryService.adCountByCategory(category));
        }
        if (empty) {
            logService.logRequest("/category", true);

            return ResponseEntity.ok(new CategoryListResponseDTO(categories.stream().map(category -> {
                long adCount = categoryService.adCountByCategory(category);
                return new CategoryResponseCountDTO(category, adCount);
            }).collect(Collectors.toList())));
        } else {
            CategoryListResponseDTO response = new CategoryListResponseDTO(categories.stream()
                    .filter(category -> !category.getAds().isEmpty())
                    .map(category -> {
                        long adCount = categoryService.adCountByCategory(category);

                        return new CategoryResponseCountDTO(category, adCount);
                    })
                    .collect(Collectors.toList()));

            logService.logRequest("/category", true);
            return ResponseEntity.ok(response);
        }
    }

    @Operation(
            summary = "Creates a new watchdog",
            description = "Creates a new watchdog " +
                    "to track ads in a specific category " +
                    "containing specific keyword " +
                    "and is under a certain price."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Watchdog created successfully."),
            @ApiResponse(responseCode = "400",
                    description = "Category with the given ID was not found, " +
                            "user does not exist.")
    })
    @Parameter(name = "watchdogDTO", description = "Watchdog data to create")
    @PostMapping("/advertisement/watch")
    public ResponseEntity<?> setUpWatchdog(
            @Valid @RequestBody WatchdogDTO watchdogDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            logService.logRequest("/advertisement/watch", false);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(watchdogService.buildErrorJsonResponseforWatchdog(bindingResult));
        }
        return watchdogService.setUpWatchdog(watchdogDTO, userDetails);
    }

    @Operation(
            summary = "Buy a digital product.",
            description = "Purchase a digital product " +
                        "and email the product key to the buyer."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful digital product purchase."),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input, " +
                            "advertisement with the given ID does not exist, " +
                            "user can’t buy his own product, " +
                            "admin user is not available")
    })
    @Parameter(name = "id", description = "Advertisement id to buy")
    @Parameter(name = "DigitalProductChargeDTO", description = "Payment data to purchase")
    @PostMapping("/advertisement/{id}/buy")
    public ResponseEntity<?> buyDigitalProduct(
            @PathVariable UUID id,
            @RequestBody DigitalProductChargeDTO digitalProductChargeDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            logService.logRequest("/advertisement/{id}/buy", false);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(stripeService.buildErrorJsonResponseForDigitalProduct(bindingResult));
        }

        User user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
        Optional<DigitalProduct> optionalDigitalProduct = adService.findDigitalProductById(id);

        if (optionalDigitalProduct.isEmpty()) {
            logService.logRequest("/advertisement/{id}/buy", false);

            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("Advertisement with the given ID does not exist."));
        }
        DigitalProduct digitalProduct = optionalDigitalProduct.orElseThrow();

        if (user == digitalProduct.getUser()) {
            logService.logRequest("/advertisement/{id}/buy", false);

            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("User can’t buy his own product."));
        }

        digitalProductChargeDTO.setAmount((int) Math.round(digitalProduct.getPrice()*100));
        StripeChargeDTO stripeChargeDTO = stripeService.charge(digitalProductChargeDTO, bindingResult);

        return adService.digitalProductPayment(stripeChargeDTO, user, digitalProduct);
    }
}




