package com.freshcart.order_service.controller.orderController;

import com.freshcart.order_service.data.request.OrderRequest;
import com.freshcart.order_service.data.response.OrderResponse;
import com.freshcart.order_service.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{id}")
    public OrderResponse getOrder(
            @PathVariable long id
    ) {
        OrderResponse order = orderService.getOrder(id);
        return order;
    }
    @GetMapping("/list")
    public List<OrderResponse> getOrdersList() {
        return orderService.getOrderList();

    }

    @PostMapping
    public ResponseEntity<OrderResponse> saveOrder(
            @RequestBody  OrderRequest order
    ) {
        OrderResponse response = orderService.createOrder(order);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity deleteOrder(
            @PathVariable(name = "orderId", required = true) long orderId
    ) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok("Order Was Canceled");
    }

}
