package com.freshcart.order_service.data.response;

import com.freshcart.order_service.data.utils.IdName;
import com.freshcart.order_service.entity.order.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse{
    private long id;
    private IdName user;
    private IdName slot;
    private String orderStatus;
    private List<OrderItemResponse> orderItems;
    private BigDecimal totalPrice;
    private LocalDateTime creationDate;
    public OrderResponse() {
    }

    public OrderResponse(long id, IdName user, IdName slot, String orderStatus, BigDecimal totalPrice,LocalDateTime creationDate,List<OrderItemResponse> orderItems) {
        this.id = id;
        this.user = user;
        this.slot = slot;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
        this.creationDate = creationDate;
        this.orderItems = orderItems;
    }


    public OrderResponse(long id, OrderStatus orderStatus, BigDecimal totalPrice) {
        this.id = id;
        this.orderStatus = orderStatus.toString();
        this.totalPrice = totalPrice;
    }
}
