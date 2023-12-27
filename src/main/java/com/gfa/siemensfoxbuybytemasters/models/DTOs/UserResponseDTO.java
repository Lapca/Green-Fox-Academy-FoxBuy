package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import com.gfa.siemensfoxbuybytemasters.models.User;

import java.util.UUID;

public class UserResponseDTO {

    private String username;

    private UUID id;

    public UserResponseDTO() {

    }

    public UserResponseDTO(User user) {
        this.username = user.getUsername();
        this.id = user.getId();
    }

    public UserResponseDTO(String username, UUID id) {
        this.username = username;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
