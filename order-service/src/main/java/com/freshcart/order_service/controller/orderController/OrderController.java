package com.freshcart.order_service.controller.orderController;

import com.freshcart.order_service.data.request.OrderRequest;
import com.freshcart.order_service.data.response.OrderResponse;
import com.freshcart.order_service.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{id}")
    public OrderResponse getOrder(
            @PathVariable long id,
            JwtAuthenticationToken jwt

    ) {
        OrderResponse order = orderService.getOrder(id,jwt.getName());
        return order;
    }
    @GetMapping("/list")
    public List<OrderResponse> getOrdersList(
            JwtAuthenticationToken jwt

    ) {
        return orderService.getOrderList(jwt.getName());

    }

    @PostMapping
    public ResponseEntity<OrderResponse> saveOrder(
            @RequestBody  OrderRequest order,
            JwtAuthenticationToken jwt
            ) {
        String email = jwt.getName();
        OrderResponse response = orderService.createOrder(order,email);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity deleteOrder(
            @PathVariable(name = "orderId", required = true) long orderId,
            JwtAuthenticationToken jwt

    ) {
        orderService.deleteOrder(orderId,jwt.getName());
        return ResponseEntity.ok("Order Was Canceled");
    }

}
