package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import com.gfa.siemensfoxbuybytemasters.models.Ad;

import java.util.UUID;

public class AdResponseDTO {

    private UUID id;
    private String title;
    private String description;
    private double price;
    private String zipcode;
    private long categoryID;



    public AdResponseDTO() {
    }

    public AdResponseDTO(UUID id, String title, String description, double price, String zipcode, long categoryID) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.zipcode = zipcode;
        this.categoryID = categoryID;
    }

    public AdResponseDTO(Ad ad) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.description = ad.getDescription();
        this.price = ad.getPrice();
        this.zipcode = ad.getZipcode();
        this.categoryID = ad.getCategory().getId();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(long categoryID) {
        this.categoryID = categoryID;
    }
}
