package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import jakarta.validation.constraints.*;

public class UserRegisterDTO {

//        @NotNull
        @NotBlank
        private String username;
        @NotNull
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")
        private String password;
        @NotNull
        @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE)
        private String email;

        public UserRegisterDTO() {
        }

        public UserRegisterDTO(String username, String password, String email) {
            this.username = username;
            this.password = password;
            this.email = email;
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
}
