package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class AdDTO {

    @NotBlank(message = "Title field can not be empty!")
    private String title;
    @NotBlank(message = "Description field can not be empty!")
    private String description;

    @NotNull(message = "Price field can not be empty!")
    private double price;
    @NotBlank(message = "Zipcode field can not be empty!")
    @Size(max = 5, message = "Zipcode can not be longer than 5 characters!")
    private String zipcode;

    @NotNull(message = "Category field can not be empty!")
    private long categoryID;

    private String productKey;

    public AdDTO() {
    }


    public AdDTO(String title, String description, double price, String zipcode, long categoryID) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.zipcode = zipcode;
        this.categoryID = categoryID;
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
