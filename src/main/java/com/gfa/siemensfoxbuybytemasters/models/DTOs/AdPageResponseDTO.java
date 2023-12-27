package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AdPageResponseDTO {

    private int page;
    @JsonProperty("total_pages")
    private int totalPages;
    @JsonProperty("Ads")
    private List<AdResponseDTO> ads;

    public AdPageResponseDTO() {
    }

    public AdPageResponseDTO(int page, int totalPages, List<AdResponseDTO> ads) {
        this.page = page;
        this.totalPages = totalPages;
        this.ads = ads;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<AdResponseDTO> getAds() {
        return ads;
    }

    public void setAds(List<AdResponseDTO> ads) {
        this.ads = ads;
    }
}
