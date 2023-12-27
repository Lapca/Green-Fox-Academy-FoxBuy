package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import java.time.LocalDateTime;

public class LogDTO {

    private String timestamp;
    private String endpoint;
    private String type;
    private String data;

    public LogDTO() {
    }

    public LogDTO(String timestamp, String endpoint, String type, String data) {
        this.timestamp = timestamp;
        this.endpoint = endpoint;
        this.type = type;
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
