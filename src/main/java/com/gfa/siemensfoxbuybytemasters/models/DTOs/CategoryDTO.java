package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import com.gfa.siemensfoxbuybytemasters.models.Category;
import jakarta.validation.constraints.NotBlank;

public class CategoryDTO {

    @NotBlank(message = "Field name is empty!")
    private String name;
    private String description;

    public CategoryDTO() {
    }

    public CategoryDTO(Category category) {
        this.name = category.getName();
        this.description = category.getDescription();
    }

    public CategoryDTO(String name, String description) {
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
}
