package com.freshcart.order_service.data.request;

import com.freshcart.order_service.data.utils.IdName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class OrderItemRequest {
    private int quantity;
    private long product;

    public OrderItemRequest() {
    }

    public OrderItemRequest( int quantity, long product) {
        this.quantity = quantity;
        this.product = product;
    }


}
