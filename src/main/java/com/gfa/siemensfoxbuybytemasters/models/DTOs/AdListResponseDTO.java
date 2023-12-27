package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gfa.siemensfoxbuybytemasters.models.Ad;

import java.util.List;

public class AdListResponseDTO {
//    private String ads = "Ads";
@JsonProperty("Ads")
private List<AdResponseDTO> ads;

    public AdListResponseDTO(List<AdResponseDTO> ads) {
        this.ads = ads;
    }

    public AdListResponseDTO() {
    }

    public List<AdResponseDTO> getAds() {
        return ads;
    }

    public void setAds(List<AdResponseDTO> ads) {
        this.ads = ads;
    }
}
