package com.freshcart.order_service.repository.orderRepository;

import com.freshcart.order_service.entity.order.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity,Long> {

     List<OrderItemEntity> findAllByOrder_IdAndDeletedIsFalse(long orderId);
     OrderItemEntity findFirstByOrder_IdAndDeletedIsFalse(long orderId);
     OrderItemEntity findFirstByIdAndDeletedIsFalse(long orderItemId);
}
