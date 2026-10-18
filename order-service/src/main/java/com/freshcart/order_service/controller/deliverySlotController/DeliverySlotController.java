package com.freshcart.order_service.controller.deliverySlotController;

import com.freshcart.order_service.data.response.DeliverySlotResponse;
import com.freshcart.order_service.service.deliverySlot.DeliverySlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/slot")
public class DeliverySlotController {

    @Autowired
    private DeliverySlotService deliverySlotService;

    @GetMapping("/{id}")
    public DeliverySlotResponse deliverySlot(
            @PathVariable long id
    ) {
        return deliverySlotService.getDeliverySlotById(id);
    }
//    @GetMapping("/list")
//    public List<DeliverySlotResponse> deliverySlotsList() {
//        return deliverySlotService.getDeliverySlotList();
//
//    }
    @GetMapping("/available/list")
    public List<DeliverySlotResponse> avaliableDeliverySlotsList() {
        return deliverySlotService.getAvaliableDeliverySlotsList();

    }



}
