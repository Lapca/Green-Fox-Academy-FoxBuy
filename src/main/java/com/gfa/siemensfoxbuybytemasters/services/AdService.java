package com.gfa.siemensfoxbuybytemasters.services;

import com.gfa.siemensfoxbuybytemasters.models.Ad;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.AdListResponseDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.AdResponseDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.StripeChargeDTO;
import com.gfa.siemensfoxbuybytemasters.models.DigitalProduct;
import com.gfa.siemensfoxbuybytemasters.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface AdService {

    Ad saveAd(Ad ad);

    DigitalProduct saveDigitalProduct(DigitalProduct digitalProduct);

    List<Ad> findAllByUser(User user);

    boolean allowedToCreateMoreAd(User user);

    Map<String, String> buildErrorJsonResponseforAd(BindingResult bindingResult);

    List<Ad> findAll();

    Optional<Ad> findAdById(UUID id);

    Optional<DigitalProduct> findDigitalProductById(UUID id);

    void deleteAd(Ad ad);

    List<AdResponseDTO> listAdsByUser(User user);

    Page<Ad> getAdsByCategoryAndPage(long id, int page);

    ResponseEntity<?> getAdsByUser(String user);

    ResponseEntity<?> getAdsByCategory(long category, int page);

    List<Ad> searchAdsByKeyword(String keyword);

    ResponseEntity<?> digitalProductPayment(
            StripeChargeDTO stripeChargeDTO, User user,
            DigitalProduct digitalProduct);

    void moveAdsToBannedAds(User user);

    void restoreBannedAdsToAds(User user);

    boolean existsAdBanned(User user);

}
