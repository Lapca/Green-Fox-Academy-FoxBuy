package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import java.util.List;

public class CategoryListResponseDTO {

    private List<CategoryResponseCountDTO> categories;

    public CategoryListResponseDTO() {};

    public CategoryListResponseDTO(List<CategoryResponseCountDTO> list) {
        this.categories = list;
    }

    public List<CategoryResponseCountDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryResponseCountDTO> categories) {
        this.categories = categories;
    }
}
