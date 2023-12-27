package com.gfa.siemensfoxbuybytemasters.services;

import com.gfa.siemensfoxbuybytemasters.models.DTOs.RatingReactionDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.RatingRequestDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.RatingResponseDTO;
import com.gfa.siemensfoxbuybytemasters.models.Rating;
import com.gfa.siemensfoxbuybytemasters.models.User;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface RatingService {

    Map<String, String> buildErrorJsonResponseForRating(BindingResult bindingResult);

    Map<String, String> buildErrorJsonResponseForRatingReaction(BindingResult bindingResult);

    boolean existsRatingByUserAndAuthorID(User user, UUID authorID);

    Rating saveRating(RatingRequestDTO ratingRequestDTO, User author, User user);

    void sendRatingNotification(User user);

    Optional<Rating> findRatingById(long id);

    void saveReactionToRating(Rating rating, RatingReactionDTO ratingReactionDTO);

    double findAverageRatingByUserId(UUID id);

    List<Rating> findAllByUser(User user);

    List<RatingResponseDTO> convertToListDTO(List<Rating> ratings);

    RatingResponseDTO deleteRating(Rating rating);

}
