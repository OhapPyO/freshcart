package com.freshcart.order_service.service.deliverySlot;

import com.freshcart.order_service.data.response.DeliverySlotResponse;
import com.freshcart.order_service.entity.delivery.DeliverySlotEntity;
import com.freshcart.order_service.entity.order.OrderStatus;
import com.freshcart.order_service.exception.ResourceNotFoundException;
import com.freshcart.order_service.repository.slotRepository.DeliverySlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeliverySlotService {

    @Autowired
    DeliverySlotRepository deliverySlotRepository;

    public DeliverySlotResponse getDeliverySlotById(long id) {
        DeliverySlotEntity deliverySlot = deliverySlotRepository.findByIdAndDeletedIsFalse(id);
        if (deliverySlot == null) {
            throw new ResourceNotFoundException("deliverySlot is null");
        }
        return new DeliverySlotResponse(deliverySlot);
    }

    public List<DeliverySlotResponse> getAvaliableDeliverySlotsList() {
        List<DeliverySlotEntity> productList = deliverySlotRepository.findAvailableDeliverySlots(
                LocalDateTime.now(),
                OrderStatus.CANCELLED
        );
        ;
        if (productList.isEmpty()) {
            return new ArrayList<>();
        }
        return productList
                .stream()
                .map(
                        m -> new DeliverySlotResponse(m.getId(), m.getStartTime(), m.getEndTime(), m.getMaxOrder(), m.getCreationDate())
                ).toList();
    }
}
