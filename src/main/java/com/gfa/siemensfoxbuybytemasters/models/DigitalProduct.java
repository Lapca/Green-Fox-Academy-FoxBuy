package com.gfa.siemensfoxbuybytemasters.models;

import com.gfa.siemensfoxbuybytemasters.models.DTOs.AdDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "digital_products")
public class DigitalProduct extends Ad{

    private String productKey;

    public DigitalProduct() {
    }

    public DigitalProduct(AdDTO adDTO, User user, Category category, String productKey) {
        super(adDTO, user, category);
        this.productKey = productKey;
    }

    public DigitalProduct(String title, String description, double price, String zipcode, User user, Category category, String productKey) {
        super(title, description, price, zipcode, user, category);
        this.productKey = productKey;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

}
