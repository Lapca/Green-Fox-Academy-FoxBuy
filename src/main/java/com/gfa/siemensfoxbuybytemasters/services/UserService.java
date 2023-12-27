package com.gfa.siemensfoxbuybytemasters.services;

import com.gfa.siemensfoxbuybytemasters.models.DTOs.BanRequestDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.StripeChargeDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.UserRegisterDTO;
import com.gfa.siemensfoxbuybytemasters.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

//    boolean isUsernameUnique(String username);

    boolean existsByEmail(String email);

    Map <String, String> buildErrorJsonResponseforRegistration(BindingResult bindingResult);

    Map <String, String> buildErrorJsonResponseforLogin(BindingResult bindingResult);

    boolean existsByUsername(String username);

    User registerUser(UserRegisterDTO userRegisterDTO);

    Optional<User> findByUsername(String username);

    Optional<User> findUserByRoles(String role);

    String generateEmailVerificationToken();
  
    boolean verifyEmail(String token);

    void saveUser(User user);

    ResponseEntity<?> vipPaymentResponse(User user, StripeChargeDTO stripeChargeDTO);

    Map <String, String> buildErrorJsonResponseforBanUser(BindingResult bindingResult);

    Optional<User> getUserByID(UUID id);

    void banUser(User user, BanRequestDTO banRequestDTO);

    void setBannedUntilToNull(User user);

}
