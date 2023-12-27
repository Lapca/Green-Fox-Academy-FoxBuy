package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class RatingRequestDTO {
    @Min(1)
    @Max(5)
    private int rating;

    @NotBlank(message = "Please provide comments.")
    private String comment;

    public RatingRequestDTO() {
    }

    public RatingRequestDTO(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
