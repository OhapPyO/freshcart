package com.freshcart.order_service.entity.delivery;

import com.freshcart.order_service.entity.BaseEntity;
import com.freshcart.order_service.entity.order.OrderEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "delivery_slot")
public class DeliverySlotEntity extends BaseEntity {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int maxOrder;

    public DeliverySlotEntity() {
    }

    public DeliverySlotEntity(LocalDateTime startTime, LocalDateTime endTime, int maxOrder) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxOrder = maxOrder;
    }


    @OneToMany(mappedBy = "slot")
    private List<OrderEntity> orderEntities;


}
