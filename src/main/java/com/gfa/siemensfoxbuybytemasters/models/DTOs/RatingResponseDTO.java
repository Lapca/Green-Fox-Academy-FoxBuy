package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class RatingResponseDTO {
    @JsonProperty("Id")
    private long id;
    @JsonProperty("Rating")
    private int rating;
    @JsonProperty("Comment")
    private String comment;
    @JsonProperty("Reaction")
    private String reaction = "";

    public RatingResponseDTO() {
        this.reaction = "";
    }

    public RatingResponseDTO(long id, int rating, String comment, String reaction) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.reaction = reaction;
    }

    public RatingResponseDTO(long id, int rating, String comment) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.reaction = "";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }
}
