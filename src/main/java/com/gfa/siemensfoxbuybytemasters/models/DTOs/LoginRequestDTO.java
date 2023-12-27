package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class LoginRequestDTO {

    @NotNull(message = "Field username was empty!")
    @NotEmpty(message = "Field username was empty!")
    private String username;

    @NotNull(message = "Field password was empty!")
    @NotEmpty(message = "Field password was empty!")
    private String password;

    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
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



}
