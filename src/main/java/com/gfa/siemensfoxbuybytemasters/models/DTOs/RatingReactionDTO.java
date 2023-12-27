package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import jakarta.validation.constraints.NotBlank;

public class RatingReactionDTO {
    @NotBlank
    private String reaction;

    public RatingReactionDTO() {
    }

    public RatingReactionDTO(String reaction) {
        this.reaction = reaction;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }
}
