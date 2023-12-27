package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BanRequestDTO {
    @Min(1)
    private long duration;

    public BanRequestDTO() {
    }

    public BanRequestDTO(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
