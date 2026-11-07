package com.freshcart.order_service.controller.productController;

import com.freshcart.order_service.data.request.ProductRequest;
import com.freshcart.order_service.data.response.DeliverySlotResponse;
import com.freshcart.order_service.data.response.OrderResponse;
import com.freshcart.order_service.data.response.ProductResponse;
import com.freshcart.order_service.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;



    @GetMapping("/{id}")
    public ProductResponse getProductById(
            @PathVariable long id
    ) {
        return productService.getProductById(id);
    }
    @GetMapping
    public List<ProductResponse> getProductList(
    ){
        return productService.getProductList();
    }

    @PostMapping
    public ResponseEntity<String> saveProduct(
            @RequestBody ProductRequest productRequest

    ){
        productService.saveProduct(productRequest);
        return ResponseEntity.status(201).body("product saved successfully");

    }
}
