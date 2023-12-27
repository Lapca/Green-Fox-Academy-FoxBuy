package com.gfa.siemensfoxbuybytemasters.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ads_banned")
public class AdBanned {

    @Id
    @GeneratedValue
    private long id;
    @Column(name = "add_id")
    private UUID adID;
    @NotBlank
    private String title;
    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String description;
    @NotNull
    private double price;
    private LocalDateTime localDateTime;
    @NotBlank
    @Size(max = 5)
    private String zipcode;
    @Column(name = "user_id")
    private UUID userID;
    @Column(name = "category_id")
    private long categoryID;

    public AdBanned() {
    }

    public AdBanned(UUID adID, String title, String description, double price, LocalDateTime localDateTime, String zipcode, UUID userID, long categoryID) {
        this.adID = adID;
        this.title = title;
        this.description = description;
        this.price = price;
        this.localDateTime = localDateTime;
        this.zipcode = zipcode;
        this.userID = userID;
        this.categoryID = categoryID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getAdID() {
        return adID;
    }

    public void setAdID(UUID adID) {
        this.adID = adID;
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

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(long categoryID) {
        this.categoryID = categoryID;
    }
}
