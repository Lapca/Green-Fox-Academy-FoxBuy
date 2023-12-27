package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import jakarta.validation.constraints.NotBlank;

public class ChargeDTO {

    private String cardToken;

    private String currency;

    private String description;

    private Integer amount;


    @NotBlank(message = "Country field can not be empty!")
    private String addressCountry;

    @NotBlank(message = "Zipcode  field can not be empty!")
    private String addressZipcode;

    @NotBlank(message = "City field can not be empty!")
    private String addressCity;

    @NotBlank(message = "Address line can not be empty!")
    private String addressLine;

    public ChargeDTO() {
    }

    public ChargeDTO(String cardToken, String currency, String description, Integer amount,
                     String addressCountry, String addressZipcode, String addressCity, String addressLine) {
        this.cardToken = cardToken;
        this.currency = currency;
        this.description = description;
        this.amount = amount;
        this.addressCountry = addressCountry;
        this.addressZipcode = addressZipcode;
        this.addressCity = addressCity;
        this.addressLine = addressLine;
    }

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }

    public String getAddressZipcode() {
        return addressZipcode;
    }

    public void setAddressZipcode(String addressZipcode) {
        this.addressZipcode = addressZipcode;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }
}