package com.gfa.siemensfoxbuybytemasters.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(unique = true)
    private String username;
    private String password;
    @Column(unique = true)
    private String email;
    private boolean emailVerified;
    private String emailVerificationToken;

    private LocalDateTime bannedUntil;

    private String refreshToken;
    private double wallet = 0.0;


    @OneToMany(mappedBy = "user")
    private List<Ad> ads = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Watchdog> watchdogList = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Rating> ratings = new ArrayList<>();

    private String roles = "ROLE_USER";

    public User() {
    }

    public User(String username, String password,
                String email, String emailVerificationToken, String roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.emailVerified = false;
        this.emailVerificationToken = emailVerificationToken;
        this.roles = roles;
    }


    public User(String username, String password,
                String email, String emailVerificationToken) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.emailVerified = false;
        this.emailVerificationToken = emailVerificationToken;
    }

    public double getWallet() {
        return wallet;
    }

    public void setWallet(double wallet) {
        this.wallet = wallet;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getEmailVerificationToken() {
        return emailVerificationToken;
    }

    public void setEmailVerificationToken(String emailVerificationToken) {
        this.emailVerificationToken = emailVerificationToken;
    }

    public List<Ad> getAds() {
        return ads;
    }

    public void setAds(List<Ad> ads) {
        this.ads = ads;
    }


    public LocalDateTime getBannedUntil() {
        return bannedUntil;
    }

    public void setBannedUntil(LocalDateTime bannedUntil) {
        this.bannedUntil = bannedUntil;
    }


    public List<Watchdog> getWatchdogList() {
        return watchdogList;
    }

    public void setWatchdogList(List<Watchdog> watchdogList) {
        this.watchdogList = watchdogList;
    }
      

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
