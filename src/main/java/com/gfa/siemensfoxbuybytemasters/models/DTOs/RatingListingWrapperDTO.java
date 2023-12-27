package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gfa.siemensfoxbuybytemasters.models.Rating;

import java.util.List;

public class RatingListingWrapperDTO {
    @JsonProperty("Average")
    private double average;
    @JsonProperty("Ratings")
    private List<RatingResponseDTO> ratingList;

    public RatingListingWrapperDTO() {
    }

    public RatingListingWrapperDTO(double average, List<RatingResponseDTO> ratingList) {
        this.average = average;
        this.ratingList = ratingList;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public List<RatingResponseDTO> getRatingList() {
        return ratingList;
    }

    public void setRatingList(List<RatingResponseDTO> ratingList) {
        this.ratingList = ratingList;
    }
}
