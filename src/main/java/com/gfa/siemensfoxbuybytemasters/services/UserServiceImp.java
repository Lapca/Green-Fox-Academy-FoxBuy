package com.gfa.siemensfoxbuybytemasters.services;

import com.gfa.siemensfoxbuybytemasters.models.DTOs.BanRequestDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.MessageDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.StripeChargeDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.UserRegisterDTO;
import com.gfa.siemensfoxbuybytemasters.models.User;
import com.gfa.siemensfoxbuybytemasters.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;



    @Autowired
    public UserServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Map<String, String> buildErrorJsonResponseforRegistration(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            if (fieldError.getField().equals("username")) {
                errors.put("errorUsername", "Username is missing or empty!");
            }
            if (fieldError.getField().equals("password")) {
                errors.put("errorPassword", "Password is missing or not well-formed!");
            }
            if (fieldError.getField().equals("email")) {
                errors.put("errorEmail", "Email is missing or not well-formed!");
            }
        }
        return errors;
    }

    @Override
    public Map<String, String> buildErrorJsonResponseforLogin(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            if (fieldError.getField().equals("username") || fieldError.getField().equals("password")) {
                errors.put("error", "Field username and/or field password was empty!");
            }
        }
        return errors;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User registerUser(UserRegisterDTO userRegisterDTO) {
        if (userRepository.count() == 0) {
            return userRepository.save(new User(userRegisterDTO.getUsername(), passwordEncoder.encode(userRegisterDTO.getPassword()), userRegisterDTO.getEmail(), generateEmailVerificationToken(), "ROLE_ADMIN"));
        }
        return userRepository.save(new User(userRegisterDTO.getUsername(), passwordEncoder.encode(userRegisterDTO.getPassword()), userRegisterDTO.getEmail(), generateEmailVerificationToken(), "ROLE_USER"));

    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findUserByRoles(String role) { return userRepository.findUserByRoles(role); }

    @Override
    public String generateEmailVerificationToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean verifyEmail(String token) {
        Optional<User> optionalUser = userRepository.findUserByEmailVerificationToken(token);
        if (optionalUser.isEmpty()) {
            return false; // TODO error handling
        }
        User user = optionalUser.get();
        user.setEmailVerified(true);
        userRepository.save(user);
        return true;
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public ResponseEntity<?> vipPaymentResponse(User user, StripeChargeDTO stripeChargeDTO) {
        if (stripeChargeDTO.getSuccess()) {
            user.setRoles("ROLE_VIP");
            userRepository.save(user);
            MessageDTO messageDTO = new MessageDTO("VIP upgrade is successful");
            return ResponseEntity.ok(messageDTO);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public Map<String, String> buildErrorJsonResponseforBanUser(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            if (fieldError.getField().equals("duration")) {
                errors.put("error", "Duration must be greater than or equal to 1");
            } else {
                errors.put("error", fieldError.getField());
            }
        }
        return errors;
    }

    @Override
    public Optional<User> getUserByID(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public void banUser(User user, BanRequestDTO banRequestDTO) {
            LocalDateTime localDateTime = LocalDateTime.now();
            user.setBannedUntil(localDateTime.plusDays(banRequestDTO.getDuration()));
            user.setRefreshToken("");
            userRepository.save(user);
    }

    @Override
    public void setBannedUntilToNull(User user) {
        user.setBannedUntil(null);
        userRepository.save(user);
    }
}
