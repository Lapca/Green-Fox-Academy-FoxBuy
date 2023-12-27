package com.gfa.siemensfoxbuybytemasters.models.DTOs;

import com.gfa.siemensfoxbuybytemasters.models.Category;


public class CategoryResponseCountDTO {

    private String name;
    private String description;
    private long adCount;


    public CategoryResponseCountDTO(Category category, long adCount) {
        this.name = category.getName();
        this.description = category.getDescription();
        this.adCount = adCount;
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

    public long getAdCount() {
        return adCount;
    }

    public void setAdCount(long adCount) {
        this.adCount = adCount;
    }
}
