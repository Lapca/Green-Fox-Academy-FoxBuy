package com.gfa.siemensfoxbuybytemasters.services;

import com.gfa.siemensfoxbuybytemasters.models.Category;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.*;
import com.gfa.siemensfoxbuybytemasters.models.User;
import com.gfa.siemensfoxbuybytemasters.models.Watchdog;
import com.gfa.siemensfoxbuybytemasters.repositories.WatchdogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class WatchdogServiceImp implements WatchdogService{

    private final WatchdogRepository watchdogRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final LogService logService;
    private final EmailService emailService;

    @Autowired
    public WatchdogServiceImp(
            WatchdogRepository watchdogRepository, CategoryService categoryService,
            UserService userService, LogService logService, EmailService emailService) {

        this.watchdogRepository = watchdogRepository;
        this.categoryService = categoryService;
        this.userService = userService;
        this.logService = logService;
        this.emailService = emailService;
    }

    @Override
    public Map<String, String> buildErrorJsonResponseforWatchdog(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            if (fieldError.getField().equals("categoryID")) {
                errors.put("error", "Category ID field can not be empty!");

            }  else if (fieldError.getField().equals("maxPrice")) {
                        errors.put("error", "Maximum price field can not be empty!");
            }
        }
        return errors;
    }

    @Override
    public Watchdog saveWatchdog(Watchdog watchdog) {
        return watchdogRepository.save(watchdog);
    }

    @Override
    public ResponseEntity<?> setUpWatchdog(
            WatchdogDTO watchdogDTO,
            UserDetails userDetails) {

        Optional<Category> optionalCategory = categoryService.findCategoryById(watchdogDTO.getCategoryID());

        if (optionalCategory.isEmpty()) {
            logService.logRequest("/advertisement/watch", false);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorDTO("Category with the given ID was not found!"));
        }

        Category category = optionalCategory.get();
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());

        if (optionalUser.isEmpty()) {
            logService.logRequest("/advertisement/watch", false);

            return ResponseEntity.badRequest().body(new ErrorDTO("User does not exist!"));
        }

        User user = optionalUser.get();
        Watchdog watchdog = new Watchdog(watchdogDTO.getMaxPrice(), watchdogDTO.getKeyword(), user, category);

        logService.logRequest("/advertisement/watch", true);
        return ResponseEntity.ok().body(new WatchdogResponseDTO(saveWatchdog(watchdog)));
    }

    @Override
    public void checkWatchdogs(AdDTO adDTO) {
        long categoryId = adDTO.getCategoryID();
        double price = adDTO.getPrice();
        String titleAndDescriptionOfAd = adDTO.getTitle() + " " + adDTO.getDescription();

        List<String> userEmails =
                watchdogRepository.findMatchingWatchdogs(categoryId, price,titleAndDescriptionOfAd);

        if(!userEmails.isEmpty()) {
            emailService.sendEmailToUser(userEmails);
        }
    }




}
