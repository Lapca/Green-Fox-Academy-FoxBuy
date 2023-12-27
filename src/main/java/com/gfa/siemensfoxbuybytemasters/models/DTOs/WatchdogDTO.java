package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class WatchdogDTO {
    @JsonProperty("category_id")
    @NotNull(message = "Category field can not be empty!")
    private Long categoryID;
    @JsonProperty("max_price")
    @NotNull(message = "Max price field can not be empty!")
    private Double maxPrice;
    private String keyword;

    public WatchdogDTO() {
    }

    public WatchdogDTO(long categoryID, Double maxPrice, String keyword) {
        this.categoryID = categoryID;
        this.maxPrice = maxPrice;
        this.keyword = keyword;
    }
    public WatchdogDTO(Long categoryID, Double maxPrice) {
        this.categoryID = categoryID;
        this.maxPrice = maxPrice;
    }

    public Long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Long categoryID) {
        this.categoryID = categoryID;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
