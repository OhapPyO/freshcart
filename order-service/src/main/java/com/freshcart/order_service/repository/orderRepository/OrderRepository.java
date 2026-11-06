package com.freshcart.order_service.repository.orderRepository;

import com.freshcart.order_service.entity.order.OrderEntity;
import com.freshcart.order_service.entity.order.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    OrderEntity findFirstByIdAndUserEmailAndDeletedIsFalse(long orderId, String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    OrderEntity findFirstByIdAndUserEmailAndDeletedIsFalseAndOrderStatusNotIn(long orderId, String userEmail, OrderStatus[] collection);

    @Query("SELECT COUNT(o) FROM OrderEntity o " +
            "WHERE o.slot.id = :slotId AND " +
            "  o.orderStatus != :cancelled AND o.deleted = false ")
    long findActiveOrdersCount(@Param("slotId") long slotId,
                          @Param("cancelled") OrderStatus cancelled
    );

    @Query("SELECT o FROM OrderEntity o " +
            "WHERE o.user.email = :email AND " +
            " o.deleted = false ")
    List<OrderEntity> findAllUserOrders(@Param("email") String email
    );
}
