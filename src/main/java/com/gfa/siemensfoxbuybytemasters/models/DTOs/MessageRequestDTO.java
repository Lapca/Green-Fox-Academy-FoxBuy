package com.gfa.siemensfoxbuybytemasters.models.DTOs;

public class MessageRequestDTO {
    private String message;

    public MessageRequestDTO(String message) {
        this.message = message;
    }

    public MessageRequestDTO() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
