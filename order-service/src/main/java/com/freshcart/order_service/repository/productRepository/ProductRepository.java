package com.freshcart.order_service.repository.productRepository;

import com.freshcart.order_service.entity.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity,Long> {

     ProductEntity findFirstByIdAndDeletedIsFalse(long productId);
     List<ProductEntity> findAllByDeletedIsFalse();
}
