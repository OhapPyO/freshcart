package com.freshcart.order_service.data.response;

import com.freshcart.order_service.data.utils.IdName;
import com.freshcart.order_service.entity.order.OrderItemEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderItemResponse {
    private long id;
    private int quantity;
    private BigDecimal price;
    private IdName product;
    private LocalDateTime creationDate;

    public OrderItemResponse() {
    }

    public OrderItemResponse(long id, int quantity, BigDecimal price, IdName product,LocalDateTime creationDate) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
         this.product = product;
        this.creationDate = creationDate;
    }

    public OrderItemResponse(OrderItemEntity orderItemEntity) {
        this.id = orderItemEntity.getId();
        this.quantity = orderItemEntity.getQuantity();
        this.price = orderItemEntity.getPrice();
        this.product = new IdName(orderItemEntity.getProduct().getId(),orderItemEntity.getProduct().getName());
        this.creationDate = orderItemEntity.getCreationDate();
    }


}
