package com.freshcart.order_service.controller.login;


import com.freshcart.order_service.data.request.LoginRequest;
import com.freshcart.order_service.data.request.RegisterRequest;
import com.freshcart.order_service.service.login.LoginService;
import com.freshcart.order_service.service.order.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        loginService.register(registerRequest);
        return ResponseEntity.status(201).body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest login) {
        loginService.login(login);
        return ResponseEntity.status(200).body("User logged successfully");
    }


}
