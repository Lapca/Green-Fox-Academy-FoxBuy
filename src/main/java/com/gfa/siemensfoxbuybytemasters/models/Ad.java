package com.gfa.siemensfoxbuybytemasters.models;

import com.gfa.siemensfoxbuybytemasters.models.DTOs.AdDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ads")
@Inheritance(strategy = InheritanceType.JOINED)
public class Ad {

    @Id
    @GeneratedValue
    private UUID id;
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
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Ad() {
    }

    public Ad(AdDTO adDTO, User user, Category category) {
        this.title = adDTO.getTitle();
        this.description = adDTO.getDescription();
        this.price = adDTO.getPrice();
        this.zipcode = adDTO.getZipcode();
        this.user = user;
        this.category = category;
        this.localDateTime = LocalDateTime.now();
    }

    public Ad(String title, String description, double price, String zipcode, User user, Category category) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.zipcode = zipcode;
        this.user = user;
        this.category = category;
        this.localDateTime = LocalDateTime.now();
    }

    public Ad(UUID id, String title, String description, double price, LocalDateTime localDateTime, String zipcode, User user, Category category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.localDateTime = localDateTime;
        this.zipcode = zipcode;
        this.user = user;
        this.category = category;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
