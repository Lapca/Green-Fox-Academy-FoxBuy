package com.gfa.siemensfoxbuybytemasters.repositories;

import com.gfa.siemensfoxbuybytemasters.models.Rating;
import com.gfa.siemensfoxbuybytemasters.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByAuthorID(UUID authorID);

    boolean existsRatingByUserAndAuthorID(User user, UUID authorID);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.user.id = :userId")
    Double findAverageRatingByUserId(@Param("userId") UUID userId);

    List<Rating> findAllByUser(User user);

//    For testing purposes
    Rating findFirstByOrderByIdDesc();
//    Rating findFirstByIdExistsOrderByIdDesc();




}
