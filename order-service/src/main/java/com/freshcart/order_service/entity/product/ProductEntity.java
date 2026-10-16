package com.freshcart.order_service.entity.product;

import com.freshcart.order_service.entity.BaseEntity;
import com.freshcart.order_service.entity.order.OrderItemEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Entity
@Getter
@Table(name = "product")
public class ProductEntity extends BaseEntity {

    private String name;

    private int stock;

    // it is related to price that is in the store and might be change over time
    private BigDecimal price;

    public ProductEntity() {
    }

    public ProductEntity(String name, int stock, BigDecimal price) {
        this.name = name;
        this.stock = stock;
        this.price = price;
    }


    @OneToMany(mappedBy = "product")
    private List<OrderItemEntity> productOrderItemEntities;

}
