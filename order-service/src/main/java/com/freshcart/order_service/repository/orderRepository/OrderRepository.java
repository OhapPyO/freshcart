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

    OrderEntity findFirstByIdAndDeletedIsFalse(long orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    OrderEntity findFirstByIdAndDeletedIsFalseAndOrderStatusNotIn(long orderId, OrderStatus[] collection);

    List<OrderEntity> findAllBySlot_IdAndDeletedIsFalse(long slotId);

    @Query("""
            select distinct o
            from OrderEntity o
            join fetch o.slot
            join fetch o.user
            left join fetch o.orderItems item
            left join fetch item.product
            where o.deleted = false
            """)
    List<OrderEntity> findAllByDeletedIsFalse();

    @Query("SELECT COUNT(o) FROM OrderEntity o " +
            "WHERE o.slot.id = :slotId AND " +
            "  o.orderStatus != :cancelled AND o.deleted = false ")
    long findActiveOrders(@Param("slotId") long slotId,
                          @Param("cancelled") OrderStatus cancelled
    );
}
