package com.freshcart.order_service.entity.order;

import com.freshcart.order_service.entity.BaseEntity;
import com.freshcart.order_service.entity.product.ProductEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Setter
@Entity
@Getter
@Table(name = "order_item")
public class OrderItemEntity extends BaseEntity {


    private int quantity;

    // it is related to price that user buy and user can see with what price they bought it
    // this is the initial price not (quantity * productPrice)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;


    public OrderItemEntity() {
    }

    public OrderItemEntity(OrderEntity order, ProductEntity product, int quantity, BigDecimal price) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }


}
