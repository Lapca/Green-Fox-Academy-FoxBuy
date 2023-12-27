package com.gfa.siemensfoxbuybytemasters.services;

import com.gfa.siemensfoxbuybytemasters.models.DTOs.RatingReactionDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.RatingRequestDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.RatingResponseDTO;
import com.gfa.siemensfoxbuybytemasters.models.Rating;
import com.gfa.siemensfoxbuybytemasters.models.User;
import com.gfa.siemensfoxbuybytemasters.repositories.RatingRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class RatingServiceImp implements RatingService {

    private final RatingRepository ratingRepository;
    private final EmailService emailService;

    @Autowired
    public RatingServiceImp(RatingRepository ratingRepository, EmailService emailService) {
        this.ratingRepository = ratingRepository;
        this.emailService = emailService;
    }

    @Override
    public Map<String, String> buildErrorJsonResponseForRating(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            if (fieldError.getField().equals("rating")) {
                errors.put("error", "Rating from 1 to 5 is missing!");
            }
            if (fieldError.getField().equals("comment")) {
                errors.put("error", "Comment is missing!");
            }
        }
        return errors;
    }

    @Override
    public Map<String, String> buildErrorJsonResponseForRatingReaction(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            if (fieldError.getField().equals("reaction")) {
                errors.put("error", "Reaction is missing!");
            }
        }
        return errors;
    }

    @Override
    public boolean existsRatingByUserAndAuthorID(User user, UUID authorID) {
        return ratingRepository.existsRatingByUserAndAuthorID(user, authorID);
    }

    @Override
    public Rating saveRating(RatingRequestDTO ratingRequestDTO, User author, User user) {

        Rating rating = new Rating(ratingRequestDTO.getRating(), ratingRequestDTO.getComment(), author.getId(), user);
        ratingRepository.save(rating);
        return rating;
    }

    @Override
    public void sendRatingNotification(User user) {
        String subject = "New Rating Alert";
        String body = "Your have been rated. ";

        try {
            emailService.sendHtmlEmail(List.of(user.getEmail()), subject, body);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Rating> findRatingById(long id) {
        return ratingRepository.findById(id);
    }
    @Override
    public void saveReactionToRating(Rating rating, RatingReactionDTO ratingReactionDTO) {
        rating.setReaction(ratingReactionDTO.getReaction());
        ratingRepository.save(rating);
    }
    @Override
    public double findAverageRatingByUserId(UUID id) {
        Double average = ratingRepository.findAverageRatingByUserId(id);
        if (average == null) {
            return 0.0;
        } else {
            return average;
        }
    }
    @Override
    public List<Rating> findAllByUser(User user) {
        return ratingRepository.findAllByUser(user);
    }
    @Override
    public List<RatingResponseDTO> convertToListDTO(List<Rating> ratings) {
        return ratings.stream()
                .map(o -> new RatingResponseDTO(o.getId(), o.getRating(), o.getComment(), (o.getReaction() != null) ? o.getReaction() : ""))
                .collect(Collectors.toList());
    }

    @Override
    public RatingResponseDTO deleteRating(Rating rating) {
            ratingRepository.delete(rating);
            return new RatingResponseDTO(rating.getId(), rating.getRating(), rating.getComment(), (rating.getReaction() != null) ? rating.getReaction() : "");
    }
}
