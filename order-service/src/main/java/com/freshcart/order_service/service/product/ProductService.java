package com.freshcart.order_service.service.product;

import com.freshcart.order_service.data.response.DeliverySlotResponse;
import com.freshcart.order_service.data.response.ProductResponse;
import com.freshcart.order_service.entity.delivery.DeliverySlotEntity;
import com.freshcart.order_service.entity.product.ProductEntity;
import com.freshcart.order_service.exception.ResourceNotFoundException;
import com.freshcart.order_service.repository.productRepository.ProductRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    public ProductResponse getProductById(long id) {
        ProductEntity product = productRepository.findFirstByIdAndDeletedIsFalse(id);
        if (product == null) {
            throw new ResourceNotFoundException("product is null");
        }
        return new ProductResponse(product);
    }


    public List<ProductResponse> getProductList() {
        List<ProductEntity> productList = productRepository.findAllByDeletedIsFalse();
        if (productList.isEmpty()) {
            return new ArrayList<>();
        }
       return productList
                .stream()
                .map(
                        m -> new ProductResponse(m.getId(), m.getName(), m.getStock(), m.getPrice(), m.getCreationDate())
                ).toList();
    }
}
