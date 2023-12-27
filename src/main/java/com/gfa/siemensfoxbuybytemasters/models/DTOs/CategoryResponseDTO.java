package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import com.gfa.siemensfoxbuybytemasters.models.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CategoryResponseDTO {

    private String name;
    private String description;
    private long id;

    public CategoryResponseDTO() {
    }

    public CategoryResponseDTO(Category category) {
        this.name = category.getName();
        this.description = category.getDescription();
        this.id = category.getId();
    }

    public CategoryResponseDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
