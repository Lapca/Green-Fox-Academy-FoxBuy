package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import jakarta.validation.constraints.NotBlank;

public class DigitalProductChargeDTO {

    private String cardToken;
    @NotBlank
    private String currency;
    private String description;
    private Integer amount;

    public DigitalProductChargeDTO() {
    }

    public DigitalProductChargeDTO(String cardToken, String currency, String description, Integer amount) {
        this.cardToken = cardToken;
        this.currency = currency;
        this.description = description;
        this.amount = amount;
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
}
