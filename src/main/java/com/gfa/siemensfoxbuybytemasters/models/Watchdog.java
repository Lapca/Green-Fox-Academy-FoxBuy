package com.gfa.siemensfoxbuybytemasters.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "watchdogs")
public class Watchdog {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    private double maxPrice;
    @Column(columnDefinition = "TEXT")
    private String keyword;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Watchdog() {
    }

    public Watchdog(double maxPrice, String keyword, User user, Category category) {
        this.maxPrice = maxPrice;
        this.keyword = keyword;
        this.user = user;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
