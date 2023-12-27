package com.gfa.siemensfoxbuybytemasters.services;

import com.gfa.siemensfoxbuybytemasters.models.Ad;


import com.gfa.siemensfoxbuybytemasters.models.DTOs.*;

import com.gfa.siemensfoxbuybytemasters.models.AdBanned;
import com.gfa.siemensfoxbuybytemasters.models.Category;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.AdListResponseDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.AdResponseDTO;


import com.gfa.siemensfoxbuybytemasters.models.DigitalProduct;
import com.gfa.siemensfoxbuybytemasters.models.User;
import com.gfa.siemensfoxbuybytemasters.repositories.AdBannedRepository;
import com.gfa.siemensfoxbuybytemasters.repositories.AdRepository;
import com.gfa.siemensfoxbuybytemasters.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdServiceImp implements AdService {

    private final AdRepository adRepository;
    private final UserService userService;
    private final LogService logService;
    private final EmailService emailService;
    private final AdBannedRepository adBannedRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public AdServiceImp(AdRepository adRepository, UserService userService,
                        LogService logService, AdBannedRepository adBannedRepository,
                        CategoryRepository categoryRepository, EmailService emailService) {
        this.adRepository = adRepository;
        this.userService = userService;
        this.logService = logService;
        this.adBannedRepository = adBannedRepository;
        this.categoryRepository = categoryRepository;
        this.emailService = emailService;
    }

    @Override
    public Ad saveAd(Ad ad) {
        return adRepository.save(ad);
    }

    @Override
    public DigitalProduct saveDigitalProduct(DigitalProduct digitalProduct) {
        return adRepository.save(digitalProduct);
    }

    @Override
    public List<Ad> findAllByUser(User user) {
        return adRepository.findAllByUser(user);
    }

    @Override
    public boolean allowedToCreateMoreAd(User user) {
        if (user.getRoles().equals("ROLE_USER")) {

            return adRepository.findAllByUser(user).size() < 3;

        } else return user.getRoles().equals("ROLE_ADMIN")
                || user.getRoles().equals("ROLE_VIP");


    }

    @Override
    public Map<String, String> buildErrorJsonResponseforAd(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            if (fieldError.getField().equals("title")) {
                errors.put("error", "Title field can not be empty!");

            } else if (fieldError.getField().equals("description")) {
                errors.put("error", "Description field can not be empty!");

            } else if (fieldError.getField().equals("price")) {
                errors.put("error", "Description field can not be empty!");

            } else if (fieldError.getField().equals("zipcode")) {
                errors.put("error", "Zipcode field can not be empty or longer than 5 characters!");

            } else if (fieldError.getField().equals("categoryID")) {
                errors.put("error", "Category field can not be empty!");

            }

        }
        return errors;
    }

    @Override
    public List<Ad> findAll() {
        return adRepository.findAll();
    }

    @Override
    public Optional<Ad> findAdById(UUID id) {
        return adRepository.findById(id);
    }

    @Override
    public Optional<DigitalProduct> findDigitalProductById(UUID id) { return adRepository.findDigitalProductById(id); }

    @Override
    public void deleteAd(Ad ad) {
        adRepository.delete(ad);
    }

    @Override
    public List<AdResponseDTO> listAdsByUser(User user) {
        return adRepository.findByUser(user).stream()
                .map(o -> new AdResponseDTO(o.getId(), o.getTitle(), o.getDescription(), o.getPrice(), o.getZipcode(), o.getCategory().getId()))
                .collect(Collectors.toList());

    }

    @Override
    public Page<Ad> getAdsByCategoryAndPage(long id, int page) {
        Pageable pageable = PageRequest.of(page, 2); // 2// posts per page
        return adRepository.findByCategory_Id(id, pageable);
    }

    @Override
    public ResponseEntity<?> getAdsByUser(String user) {
        Optional<User> optionalUser = userService.findByUsername(user);
        if (optionalUser.isPresent()) {
            logService.logRequest("/advertisement", true);

            List<AdResponseDTO> responseDTOList = listAdsByUser(optionalUser.get());
            return ResponseEntity.ok().body(new AdListResponseDTO(responseDTOList));
        }
        logService.logRequest("/advertisement", false);

        return ResponseEntity.badRequest().body(new ErrorDTO("User not found!"));
    }

    @Override
    public ResponseEntity<?> getAdsByCategory(long category, int page) {
        Page<Ad> ads = getAdsByCategoryAndPage(category, page - 1);

        if (ads.hasContent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("page", page);
            response.put("total_pages", ads.getTotalPages());
            response.put("ads", ads.getContent().stream()
                    .map(o -> new AdResponseDTO(o))
                    .collect(Collectors.toList()));

            logService.logRequest("/advertisement", true);

            return ResponseEntity.ok(response);
        } else {
            logService.logRequest("/advertisement", false);

            return ResponseEntity.badRequest().body(new ErrorDTO("No ads found for category: " + category));
        }
    }
    @Override
    public List<Ad> searchAdsByKeyword(String keyword) {
        return adRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }

    public void moveAdsToBannedAds(User user) {
        List<Ad> ads = adRepository.findAllByUser(user);
        for (Ad ad : ads) {
            AdBanned adBanned = new AdBanned(ad.getId(), ad.getTitle(), ad.getDescription(), ad.getPrice(), ad.getLocalDateTime(), ad.getZipcode(), ad.getUser().getId(), ad.getCategory().getId());
            adBannedRepository.save(adBanned);
            adRepository.delete(ad);
        }
    }

    @Override
    public void restoreBannedAdsToAds(User user) {
        List<AdBanned> adsBanned = adBannedRepository.findAllByUserID(user.getId());
        for (AdBanned adBanned : adsBanned) {
            Optional<Category> optionalCategory = categoryRepository.findById(adBanned.getCategoryID());
            if(optionalCategory.isPresent()) {
                Ad ad = new Ad(adBanned.getAdID(), adBanned.getTitle(), adBanned.getDescription(), adBanned.getPrice(), adBanned.getLocalDateTime(), adBanned.getZipcode(), user, optionalCategory.get());
                adRepository.save(ad);
                adBannedRepository.delete(adBanned);
            }
        }
    }
    @Override
    public boolean existsAdBanned(User user) {
        List<AdBanned> adsBanned = adBannedRepository.findAllByUserID(user.getId());
            return adsBanned.size() > 0;
    }

    @Override
    public ResponseEntity<?> digitalProductPayment(
            StripeChargeDTO stripeChargeDTO, User user,
            DigitalProduct digitalProduct) {

        if(stripeChargeDTO.getSuccess()) {
            digitalProduct.getUser().setWallet(digitalProduct.getUser().getWallet() + stripeChargeDTO.getAmount() * 0.7);

            Optional<User> optionalAdminUser = userService.findUserByRoles("ROLE_ADMIN");
            if(optionalAdminUser.isEmpty()) {
                logService.logRequest("/advertisement/{id}/buy", false);

                return ResponseEntity.badRequest()
                        .body(new ErrorDTO("Admin user is not available."));
            }
            User adminUser = optionalAdminUser.get();
            adminUser.setWallet(adminUser.getWallet() + stripeChargeDTO.getAmount() * 0.3);

            String productKey = digitalProduct.getProductKey();
            emailService.sendProductKeyToBuyer(user, productKey);

            deleteAd(digitalProduct);
            logService.logRequest("/advertisement/{id}/buy", true);

            MessageDTO messageDTO = new MessageDTO("Successful digital product purchase.");
            return ResponseEntity.ok(messageDTO);
        } else {
            logService.logRequest("/advertisement/{id}/buy", false);

            return ResponseEntity.badRequest().build();
            }
    }

}

