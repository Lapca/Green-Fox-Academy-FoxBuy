package com.gfa.siemensfoxbuybytemasters.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {


    @Id
    @GeneratedValue
    private long id;
    @Column(unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ad> ads = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Watchdog> watchdogList = new ArrayList<>();

    @Transient
    private long adCount = 0;

    public Category() {
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Category(String name, String description, long id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Ad> getAds() {
        return ads;
    }

    public void setAds(List<Ad> ads) {
        this.ads = ads;
    }

    public long getAdCount() {
        return adCount;
    }

    public void setAdCount(long adCount) {
        this.adCount = adCount;
    }

    public List<Watchdog> getWatchdogList() {
        return watchdogList;
    }

    public void setWatchdogList(List<Watchdog> watchdogList) {
        this.watchdogList = watchdogList;
    }
}
