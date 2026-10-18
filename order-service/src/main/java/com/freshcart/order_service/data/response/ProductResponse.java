package com.freshcart.order_service.data.response;

import com.freshcart.order_service.entity.product.ProductEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProductResponse  {
    private long id;
    private String name;
    private int stock;
    private BigDecimal price;
    private LocalDateTime creationDate;

    public ProductResponse() {
    }

    public ProductResponse(long id, String name, int stock, BigDecimal price, LocalDateTime creationDate) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.creationDate = creationDate;
    }


    public ProductResponse(ProductEntity product) {
        this.id = product.getId();
        this.name = product.getName();
        this.stock = product.getStock();
        this.price = product.getPrice();
        this.creationDate = product.getCreationDate();
    }
}
