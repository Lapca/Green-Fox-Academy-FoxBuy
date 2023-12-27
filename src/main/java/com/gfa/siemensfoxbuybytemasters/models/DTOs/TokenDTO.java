package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenDTO {

    @JsonProperty("access_token")
    private String token;
    @JsonProperty("refresh_token")
    private String refreshToken;


    public TokenDTO() {
    }

    public TokenDTO(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
