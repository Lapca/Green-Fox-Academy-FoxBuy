package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gfa.siemensfoxbuybytemasters.models.Watchdog;

public class WatchdogResponseDTO {
    @JsonProperty("category_id")
    private long categoryID;
    @JsonProperty("max_price")
    private double maxPrice;
    private String keyword;

    public WatchdogResponseDTO() {
    }

    public WatchdogResponseDTO(long categoryID, double maxPrice, String keyword) {
        this.categoryID = categoryID;
        this.maxPrice = maxPrice;
        this.keyword = keyword;
    }
    public WatchdogResponseDTO(Watchdog watchdog) {
        this.categoryID = watchdog.getCategory().getId();
        this.maxPrice = watchdog.getMaxPrice();
        this.keyword = watchdog.getKeyword();
    }

    public long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(long categoryID) {
        this.categoryID = categoryID;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
