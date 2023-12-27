package com.gfa.siemensfoxbuybytemasters.controllers;


import com.gfa.siemensfoxbuybytemasters.models.DTOs.*;
import com.gfa.siemensfoxbuybytemasters.models.Rating;
import com.gfa.siemensfoxbuybytemasters.models.User;
import com.gfa.siemensfoxbuybytemasters.services.*;
import com.gfa.siemensfoxbuybytemasters.services.EmailService;
import com.gfa.siemensfoxbuybytemasters.services.JwtService;
import com.gfa.siemensfoxbuybytemasters.services.LogService;
import com.gfa.siemensfoxbuybytemasters.services.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@SecurityRequirement(name = "javainuseapi")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final LogService logService;
    private final AdService adService;
    private final RatingService ratingService;


    @Autowired
    public UserController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          EmailService emailService,
                          LogService logService,
                          AdService adService, RatingService ratingService) {

        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.logService = logService;
        this.adService = adService;
        this.ratingService = ratingService;
    }

    @Operation(
            summary = "Registers a new user",
            description = "Registers a new user and, " +
                    "if email verification is enabled," +
                    " sends a verification email."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "User created successfully."),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input or user already exists.")
    })
    @Parameter(name = "userRegisterDTO", description = "User registration data")
    @PostMapping("/api/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody UserRegisterDTO userRegisterDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // Errors with validating/receiving data
            logService.logRequest("/api/register", false);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(userService.buildErrorJsonResponseforRegistration(bindingResult));
        }

        if (userService.existsByUsername(userRegisterDTO.getUsername())) {
            logService.logRequest("/api/register", false);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorDTO("Username already exists"));
        }

        if (userService.existsByEmail(userRegisterDTO.getEmail())) {
            logService.logRequest("/api/register", false);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorDTO("Email already exists"));
        }

        User userToSave = userService.registerUser(userRegisterDTO);
        if (Boolean.parseBoolean(System.getenv("EMAIL_VERIFICATION"))
                && !userToSave.getRoles().equals("ROLE_ADMIN")) {
            emailService.sendVerificationEmail(userToSave);
        } else {
            userService.verifyEmail(userToSave.getEmailVerificationToken());
        }

        UserResponseDTO response = new UserResponseDTO(userToSave);
        logService.logRequest("/api/register", true);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "User login",
            description = "Authenticate and login a user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Authentication successful. JWT token generated."),
            @ApiResponse(responseCode = "401",
                    description = "Authentication failed. Incorrect username and/or password."),
            @ApiResponse(responseCode = "401",
                    description = "The user is banned.")
    })
    @Parameter(name = "loginRequestDTO", description = "Login request data")
    @PostMapping("/api/login")
    public ResponseEntity<?> authenticateAndGetToken(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            logService.logRequest("/api/login", false);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(userService.buildErrorJsonResponseforLogin(bindingResult));
        }
        Optional<User> optionalUser = userService.findByUsername(loginRequestDTO.getUsername());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getBannedUntil() != null && LocalDateTime.now().isBefore(user.getBannedUntil())) {
                logService.logRequest("/api/login", false);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDTO("The user is banned."));
            }
            if (user.getBannedUntil() != null && LocalDateTime.now().isAfter(user.getBannedUntil()) && adService.existsAdBanned(user)) {
                try {
                    userService.setBannedUntilToNull(user);
                    adService.restoreBannedAdsToAds(user);
                } catch (Exception e) {
                    logService.logRequest("/api/login", false);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO(e.getMessage()));
                }
            }
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(),
                            loginRequestDTO.getPassword()));
            Map<String, String> response = new HashMap<>();
            response.put("status", "ok");
            response.put("access_token", jwtService.generateToken(loginRequestDTO.getUsername()));
            User user = userService.findByUsername(loginRequestDTO.getUsername()).orElseThrow();
            user.setRefreshToken(jwtService.generateRefreshToken(user.getUsername()));
            userService.saveUser(user);
            response.put("refresh_token", user.getRefreshToken());

            logService.logRequest("/api/login", true);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException b) {
            logService.logRequest("/api/login", false);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorDTO("Authentication failed." +
                            " Incorrect username and/or password."));
        }
    }

    @Hidden
    @GetMapping("/api/isRunning")
    public String isRunning() {
        return "Service is running";
    }

    @Hidden
    @GetMapping("/admin/isRunning")
    public String isRunningForAdmin() {
        return "Service is running";
    }

    @Hidden
    @GetMapping("/api/user")
    public String getUser(@Parameter @AuthenticationPrincipal UserDetails userDetails) {
        return userDetails.getAuthorities().toString();
    }

    @Operation(
            summary = "Verify User Email",
            description = "This endpoint is used to verify a user's email" +
                    " after clicking on the verification link" +
                    " sent to their email upon registration." +
                    " Successful verification grants access to additional features."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Email verification successful." +
                            " The user now has access to additional features."),
            @ApiResponse(responseCode = "400",
                    description = "Email verification failed." +
                            " The provided verification token is invalid or has expired.")
    })
    @Parameter(name = "token", description = "The verification token received via email.")
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        boolean verificationResult = userService.verifyEmail(token);

        if (verificationResult) {
            logService.logRequest("/verify-email", true);

            return ResponseEntity.ok("Email verification successful." +
                    "You now have access to additional features.");
        } else {
            logService.logRequest("/verify-email", false);

            return ResponseEntity.badRequest().body("Email verification failed." +
                    "The provided token is invalid or has expired.");
        }
    }

    @Operation(
            summary = "Authorize user",
            description = "Authorize a user based on a JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User authorized successfully."),
            @ApiResponse(responseCode = "400",
                    description = "User authorization failed." +
                            " Invalid token or user does not exist.")
    })
    @Parameter(name = "tokenDTO", description = "JWT token in the request body")
    @PostMapping("/auth")
    public ResponseEntity<?> identifyUser(
            @RequestBody TokenDTO tokenDTO) {
        try {
            String username = jwtService.extractUsername(tokenDTO.getToken());
            Optional<User> optionalUser = userService.findByUsername(username);

            if (optionalUser.isEmpty()) {
                logService.logRequest("/auth", false);

                return ResponseEntity.badRequest().body(new ErrorDTO("User does not exist."));
            }

            User user = optionalUser.get();
            logService.logRequest("/auth", true);

            return ResponseEntity.ok().body(new UserResponseDTO(user.getUsername(), user.getId()));

        } catch (Exception e) {
            logService.logRequest("/auth", false);

            return ResponseEntity.badRequest().body(new ErrorDTO("Invalid token."));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestBody TokenDTO tokenDTO) {
        try {
            String username = jwtService.extractUsername(tokenDTO.getRefreshToken());
            Optional<User> optionalUser = userService.findByUsername(username);

            if (optionalUser.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorDTO("User does not exist."));
            }
            User user = optionalUser.get();
            if (jwtService.validateRefreshToken(tokenDTO.getRefreshToken(), user)) {
                user.setRefreshToken(jwtService.generateRefreshToken(username));
                userService.saveUser(user);
                return ResponseEntity.ok().body(new TokenDTO(jwtService.generateToken(username), user.getRefreshToken()));
            } else {
                return ResponseEntity.badRequest().body(new ErrorDTO("Refresh token has expired."));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorDTO("Refresh token has expired."));
        }
    }

    @Operation(
            summary = "Ban user",
            description = "Ban user for a given period of time (min 1 day) and move his ads to the ads_banned table."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User has been banned until displayed time."),
            @ApiResponse(responseCode = "400",
                    description = "User doesn't exist." +
                            " Duration must be greater than or equal to 1.")
    })
    @Parameter(name = "id", description = "User uuid")
    @Parameter(name = "BanRequestDTO", description = "duration: number of days")
    @PostMapping("/user/{id}/ban")
    public ResponseEntity<?> banUser(
            @PathVariable UUID id,
            @Valid @RequestBody BanRequestDTO banRequestDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            logService.logRequest("/user/{id}/ban", false);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(userService.buildErrorJsonResponseforBanUser(bindingResult));
        }

        Optional<User> optionalUser = userService.getUserByID(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            try {
                userService.banUser(user, banRequestDTO);
                adService.moveAdsToBannedAds(user);
                logService.logRequest("/user/{id}/ban", true);
                return ResponseEntity.ok().body(new BanResponseDTO(user.getUsername(), user.getBannedUntil()));

            } catch (Exception e) {
                logService.logRequest("/user/{id}/ban", false);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO(e.getMessage()));
            }
        } else {
            logService.logRequest("/user/{id}/ban", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorDTO("User doesn't exist."));
        }
    }

    @Operation(
            summary = "User rating",
            description = "User can provide rating for another users." +
                    "User is not allowed to give rating to himself." +
                    "User need to provide number 1-5 (worst to best) and some comment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User rating has been provided."),
            @ApiResponse(responseCode = "400",
                    description = "User does not exist." +
                            "User cannot rate himself." +
                            "User can provide only one rating to another user.")
    })
    @Parameter(name = "id", description = "User uuid")
    @Parameter(name = "RatingRequestDTO", description = "rating (from 1 to 5) and comment")
    @PostMapping("/user/{id}/rating")
    public ResponseEntity<?> provideRatingForUser(
            @PathVariable UUID id,
            @Valid @RequestBody RatingRequestDTO ratingRequestDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            logService.logRequest("/user/{id}/rating", false);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ratingService.buildErrorJsonResponseForRating(bindingResult));
        }

        User author = userService.findByUsername(userDetails.getUsername()).orElse(null);
        Optional<User> optionalUser = userService.getUserByID(id);

        if (optionalUser.isEmpty()) {
            logService.logRequest("/user/{id}/rating", false);
            return ResponseEntity.badRequest().body(new ErrorDTO("User does not exists!"));
        }
        if (optionalUser.get().getId() == author.getId()) {
            logService.logRequest("/user/{id}/rating", false);
            return ResponseEntity.badRequest().body(new ErrorDTO("You cannot rate yourself!"));
        }
        if (ratingService.existsRatingByUserAndAuthorID(optionalUser.get(), author.getId())) {
            logService.logRequest("/user/{id}/rating", false);
            return ResponseEntity.badRequest().body(new ErrorDTO("You have already rated this user. Delete this rating and try again."));
        }
        try {
            Rating rating = ratingService.saveRating(ratingRequestDTO, author, optionalUser.get());
            if (Boolean.parseBoolean(System.getenv("EMAIL_VERIFICATION"))) {
                ratingService.sendRatingNotification(optionalUser.get());
            }
            logService.logRequest("/user/{id}/rating", true);
            return ResponseEntity.ok().body(new RatingResponseDTO(rating.getId(), rating.getRating(), rating.getComment()));

        } catch (Exception e) {
            logService.logRequest("/user/{id}/rating", false);
            return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
        }
    }

    @Operation(
            summary = "User's reaction to rating",
            description = "User can give reaction to rating.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Created."),
            @ApiResponse(responseCode = "400",
                    description = "Rating does not exist." +
                            "User cannot give give reaction to other user's ratings.")
    })
    @Parameter(name = "id", description = "Rating ID")
    @Parameter(name = "RatingReactionDTO", description = "reaction: text")
    @PostMapping("/user/rating/{id}")
    public ResponseEntity<?> setReactionforRating(
            @PathVariable long id,
            @Valid @RequestBody RatingReactionDTO ratingReactionDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            logService.logRequest("/user/rating/{id}", false);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ratingService.buildErrorJsonResponseForRatingReaction(bindingResult));
        }

        User user = userService.findByUsername(userDetails.getUsername()).orElse(null);
        Rating rating = ratingService.findRatingById(id).orElse(null);

        if (rating == null){
            logService.logRequest("/user/rating/{id}", false);
            return ResponseEntity.badRequest().body(new ErrorDTO("Rating doesn't exist."));
        }

        if (user.getId() != rating.getUser().getId()) {
            logService.logRequest("/user/rating/{id}", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDTO("You cannot give reaction to other user's ratings."));
        }

        try {
            ratingService.saveReactionToRating(rating, ratingReactionDTO);
            logService.logRequest("/user/rating/{id}", true);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            logService.logRequest("/user/rating/{id}", false);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Operation(
            summary = "Listing of ratings for given user",
            description = "Average rating value plus list of all ratings.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "List of ratings for given user."),
            @ApiResponse(responseCode = "400",
                    description = "User doesn't exist.")
    })
    @Parameter(name = "id", description = "User uuid")
    @GetMapping("/user/{id}/rating")
    public ResponseEntity<?> showRatingforUser(@PathVariable UUID id) {
        Optional<User> optionalUser = userService.getUserByID(id);
        if(optionalUser.isPresent()) {
            double average = ratingService.findAverageRatingByUserId(id);
            List<Rating> ratings = ratingService.findAllByUser(optionalUser.get());
            List<RatingResponseDTO> ratingResponseDTOList = ratingService.convertToListDTO(ratings);

            logService.logRequest("/user/{id}/rating", true);
            return ResponseEntity.ok().body(new RatingListingWrapperDTO(average, ratingResponseDTOList));
        } else {
            logService.logRequest("/user/{id}/rating", false);
            return ResponseEntity.badRequest().body(new ErrorDTO("User doesn't exist."));
        }
    }
    @Operation(
            summary = "Delete rating",
            description = "User can delete rating provided by him. Also admin can delete ratings.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Rating has been deleted"),
            @ApiResponse(responseCode = "400",
                    description = "Rating doesn't exist." +
                            "User does not exists." +
                            "User cannot delete other's ratings on himself.")
    })
    @Parameter(name = "id", description = "User uuid")
    @Parameter(name = "ratingId", description = "Rating ID")
    @DeleteMapping("/user/{id}/rating/{ratingId}")
    public ResponseEntity<?> deleteRating(
            @PathVariable UUID id,
            @PathVariable long ratingId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User authorizedUser = userService.findByUsername(userDetails.getUsername()).orElse(null);
        Optional<User> optionalUser = userService.getUserByID(id);
        Optional<Rating> optionalRating = ratingService.findRatingById(ratingId);

        if (optionalRating.isEmpty()) {
            logService.logRequest("/user/{id}/rating/{ratingId}", false);
            return ResponseEntity.badRequest().body(new ErrorDTO("Rating doesn't exist!"));
        }
        if (optionalUser.isEmpty()) {
            logService.logRequest("/user/{id}/rating/{ratingId}", false);
            return ResponseEntity.badRequest().body(new ErrorDTO("User does not exists!"));
        }
        if (optionalUser.get().getId() == authorizedUser.getId()) {
            logService.logRequest("/user/{id}/rating/{ratingId}", false);
            return ResponseEntity.badRequest().body(new ErrorDTO("You cannot delete other's ratings on yourself!"));
        }

        if ((optionalRating.get().getAuthorID().equals(authorizedUser.getId())) || userDetails.getAuthorities().toString().contains("ROLE_ADMIN")) {
            try {
                RatingResponseDTO ratingResponseDTO = ratingService.deleteRating(optionalRating.get());
                logService.logRequest("/user/{id}/rating/{ratingId}", true);
                return ResponseEntity.ok().body(ratingResponseDTO);
            } catch (Exception e) {
                logService.logRequest("/user/{id}/rating/{ratingId}", false);
                return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
            }
        }
        logService.logRequest("/user/{id}/rating/{ratingId}", false);
        return ResponseEntity.badRequest().body(new ErrorDTO("You cannot delete ratings provided by others!"));
    }
}