package com.freshcart.order_service.entity.order;

import com.freshcart.order_service.entity.BaseEntity;
import com.freshcart.order_service.entity.delivery.DeliverySlotEntity;
import com.freshcart.order_service.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "orders")
public class OrderEntity extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private DeliverySlotEntity slot;


    // Stored as
//    ORDER_APPROVED,
//    PACKED,
//    ON_THE_WAY,
//    CANCELLED,
//    DELIVERED,
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private BigDecimal totalPrice;

    @Version
    private Integer version;

    public OrderEntity() {
    }

    public OrderEntity(UserEntity user, DeliverySlotEntity slot, OrderStatus orderStatus, BigDecimal totalPrice) {
        this.user = user;
        this.slot = slot;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
    }


    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItemEntity> orderItems;

}
