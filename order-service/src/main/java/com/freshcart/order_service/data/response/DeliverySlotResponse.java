package com.freshcart.order_service.data.response;

import com.freshcart.order_service.entity.delivery.DeliverySlotEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DeliverySlotResponse {
    private long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int maxOrder;
    private LocalDateTime creationDate;

    public DeliverySlotResponse() {
    }
    public DeliverySlotResponse(long id, LocalDateTime startTime, LocalDateTime endTime, int maxOrder, LocalDateTime creationDate) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxOrder = maxOrder;
        this.creationDate = creationDate;
    }


    public DeliverySlotResponse(DeliverySlotEntity deliverySlot) {
        this.id = deliverySlot.getId();
        this.startTime = deliverySlot.getStartTime();
        this.endTime = deliverySlot.getEndTime();
        this.maxOrder = deliverySlot.getMaxOrder();
        this.creationDate = deliverySlot.getCreationDate();
    }
}
