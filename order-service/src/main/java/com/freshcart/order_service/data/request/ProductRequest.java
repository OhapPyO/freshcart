package com.freshcart.order_service.data.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {
    private long id;
    private String name;
    private int stock;
    private BigDecimal price;

    public ProductRequest() {
    }

    public ProductRequest(long id, String name, int stock, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public ProductRequest(String name, int stock, BigDecimal price) {
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

}
