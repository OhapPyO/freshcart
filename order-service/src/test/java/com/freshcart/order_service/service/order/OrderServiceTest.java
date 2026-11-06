package com.freshcart.order_service.service.order;

import com.freshcart.order_service.data.request.OrderItemRequest;
import com.freshcart.order_service.data.request.OrderRequest;
import com.freshcart.order_service.entity.delivery.DeliverySlotEntity;
import com.freshcart.order_service.entity.order.OrderStatus;
import com.freshcart.order_service.entity.product.ProductEntity;
import com.freshcart.order_service.entity.user.UserEntity;
import com.freshcart.order_service.exception.CustomException;
import com.freshcart.order_service.repository.orderRepository.OrderRepository;
import com.freshcart.order_service.repository.productRepository.ProductRepository;
import com.freshcart.order_service.repository.slotRepository.DeliverySlotRepository;
import com.freshcart.order_service.repository.user.UserRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DeliverySlotRepository deliverySlotRepository;

    @Test
    public void createOrder_throwsWhenSlotIsFull() {
        OrderItemRequest orderItemRequest = new OrderItemRequest(1, 1);
        List<OrderItemRequest> orderList = new ArrayList<OrderItemRequest>();
        orderList.add(orderItemRequest);
        OrderRequest orderRequest = new OrderRequest(1, orderList);
        String email = "c2test";
        when(userRepository.findFirstByEmailAndDeletedIsFalse(email)).thenReturn(
                new UserEntity()
        );
        when(deliverySlotRepository.findSlotForUpdate(orderRequest.getSlot())).thenReturn(
                new DeliverySlotEntity(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), 1
                ));
        // there is already a order for this slot from before and now we want to add another order from top to this slot
        when(orderRepository.findActiveOrdersCount(orderRequest.getSlot(), OrderStatus.CANCELLED)).thenReturn(1L);

        var ex = assertThrows(CustomException.class, () -> orderService.createOrder(orderRequest, email));
        assertEquals("Slot is Full", ex.getMessage());
        System.out.println(ex.getMessage());
    }

    @Test
    public void createOrder_throwsWhenProductIsDuplicated() {
        OrderItemRequest orderItemRequest = new OrderItemRequest(1, 1);
        List<OrderItemRequest> orderList = new ArrayList<OrderItemRequest>();
        orderList.add(orderItemRequest);
        orderList.add(orderItemRequest);
        OrderRequest orderRequest = new OrderRequest(1, orderList);
        String email = "c2test";
        var ex = assertThrows(ValidationException.class, () -> orderService.createOrder(orderRequest, email));
        assertEquals("Duplicate products in order", ex.getMessage());
        System.out.println(ex.getMessage());
    }

    @Test
    public void createOrder_throwsWhenSlotAlreadyStarted() {
        OrderItemRequest orderItemRequest = new OrderItemRequest(1, 1);
        List<OrderItemRequest> orderList = new ArrayList<OrderItemRequest>();
        orderList.add(orderItemRequest);
        OrderRequest orderRequest = new OrderRequest(1, orderList);
        String email = "c2test";
        when(userRepository.findFirstByEmailAndDeletedIsFalse(email)).thenReturn(
                new UserEntity()
        );
        when(deliverySlotRepository.findSlotForUpdate(orderRequest.getSlot())).thenReturn(
                new DeliverySlotEntity(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(3), 1
                ));

        var ex = assertThrows(ValidationException.class, () -> orderService.createOrder(orderRequest, email));
        assertEquals("you can not order,  because delivering already started ", ex.getMessage());
        System.out.println(ex.getMessage());
    }

    @Test
    public void createOrder_savesOrderWhenSlotHasRoom() {
        OrderItemRequest orderItemRequest = new OrderItemRequest(1, 1);
        List<OrderItemRequest> orderList = new ArrayList<OrderItemRequest>();
        orderList.add(orderItemRequest);
        OrderRequest orderRequest = new OrderRequest(1, orderList);
        String email = "c2test";
        when(userRepository.findFirstByEmailAndDeletedIsFalse(email)).thenReturn(
                new UserEntity()
        );
        when(deliverySlotRepository.findSlotForUpdate(orderRequest.getSlot())).thenReturn(
                new DeliverySlotEntity(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), 1
                ));
        when(orderRepository.findActiveOrdersCount(orderRequest.getSlot(), OrderStatus.CANCELLED)).thenReturn(0L);
        ProductEntity product = new ProductEntity(
                "milk",
                1,
                BigDecimal.ONE
        );
        when(productRepository.findFirstByIdAndDeletedIsFalse(orderRequest.getOrderItems().getFirst().getProduct())).thenReturn(product);

        orderService.createOrder(orderRequest, email);
        verify(orderRepository).save(any());
        assertEquals(0, product.getStock());
        System.out.println(product.getStock());
    }


    @Test
    public void createOrder_throwsWhenQuantityIsMoreThanStock() {
        OrderItemRequest orderItemRequest = new OrderItemRequest(2, 1);
        List<OrderItemRequest> orderList = new ArrayList<OrderItemRequest>();
        orderList.add(orderItemRequest);
        OrderRequest orderRequest = new OrderRequest(1, orderList);
        String email = "c2test";
        when(userRepository.findFirstByEmailAndDeletedIsFalse(email)).thenReturn(
                new UserEntity()
        );
        when(deliverySlotRepository.findSlotForUpdate(orderRequest.getSlot())).thenReturn(
                new DeliverySlotEntity(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), 1
                ));

        when(orderRepository.findActiveOrdersCount(orderRequest.getSlot(), OrderStatus.CANCELLED)).thenReturn(0L);
        ProductEntity product = new ProductEntity(
                "milk",
                1,
                BigDecimal.ONE
        );
        when(productRepository.findFirstByIdAndDeletedIsFalse(orderRequest.getOrderItems().getFirst().getProduct())).thenReturn(product);

        var ex = assertThrows(CustomException.class, () -> orderService.createOrder(orderRequest, email));
        assertEquals("Not enough products in stock", ex.getMessage());
        verify(orderRepository, never()).save(any());
        assertEquals(1, product.getStock())
    }


}
