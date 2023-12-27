package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import com.gfa.siemensfoxbuybytemasters.models.DigitalProduct;

import java.util.UUID;

public class DigitalProductDTO {
    private UUID id;
    private String title;
    private String description;
    private double price;
    private String zipcode;
    private long categoryID;
    private String productKey;

    public DigitalProductDTO() {
    }

    public DigitalProductDTO(UUID id, String title, String description, double price,
                             String zipcode, long categoryID, String productKey) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.zipcode = zipcode;
        this.categoryID = categoryID;
        this.productKey = productKey;
    }
    public DigitalProductDTO(DigitalProduct digitalProduct) {
        this.id = digitalProduct.getId();
        this.title = digitalProduct.getTitle();
        this.description = digitalProduct.getDescription();
        this.price = digitalProduct.getPrice();
        this.zipcode = digitalProduct.getZipcode();
        this.categoryID = digitalProduct.getCategory().getId();
        this.productKey = digitalProduct.getProductKey();
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

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }
}
