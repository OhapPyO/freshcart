package com.freshcart.order_service.data.request;

import com.freshcart.order_service.data.utils.IdName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private long slot;
    private List<OrderItemRequest> orderItems;


    public OrderRequest() {
    }

    public OrderRequest(long slot, List<OrderItemRequest> orderItems) {
        this.slot = slot;
        this.orderItems = orderItems;
    }


}
