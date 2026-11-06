package com.freshcart.order_service.repository;

import com.freshcart.order_service.TestcontainersConfiguration;
import com.freshcart.order_service.entity.delivery.DeliverySlotEntity;
import com.freshcart.order_service.entity.order.OrderEntity;
import com.freshcart.order_service.entity.order.OrderItemEntity;
import com.freshcart.order_service.entity.order.OrderStatus;
import com.freshcart.order_service.entity.product.ProductEntity;
import com.freshcart.order_service.entity.user.UserEntity;
import com.freshcart.order_service.repository.orderRepository.OrderRepository;
import com.freshcart.order_service.repository.productRepository.ProductRepository;
import com.freshcart.order_service.repository.slotRepository.DeliverySlotRepository;
import com.freshcart.order_service.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeliverySlotRepository deliverySlotRepository;


    @Test
    public void findActiveOrdersCount_doesNotCountCancelledOrders() {
        UserEntity user = saveUser();
        DeliverySlotEntity slot = new DeliverySlotEntity(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 2);
        deliverySlotRepository.save(slot);
        saveOrder(user, slot, OrderStatus.ORDER_APPROVED);
        saveOrder(user, slot, OrderStatus.ORDER_APPROVED);
        saveOrder(user, slot, OrderStatus.CANCELLED);

        long count = orderRepository.findActiveOrdersCount(slot.getId(), OrderStatus.CANCELLED);

        assertEquals(2, count);
        System.out.println(count);

    }

    @NonNull
    private UserEntity saveUser() {
        UserEntity user = new UserEntity();
        user.setRole("CUSTOMER");
        user.setPassword("123qwe");
        user.setEmail("aaa@gmail.com");
        user.setFirstName("aaaa");
        user.setLastName("vvvv");
        userRepository.save(user);
        return user;
    }

    private void saveOrder(UserEntity user, DeliverySlotEntity slot, OrderStatus status) {
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setOrderStatus(status);
        order.setTotalPrice(BigDecimal.TEN);
        order.setSlot(slot);
        orderRepository.save(order);
    }

}
