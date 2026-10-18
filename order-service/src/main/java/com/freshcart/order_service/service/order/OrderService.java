package com.freshcart.order_service.service.order;

import com.freshcart.order_service.data.request.OrderItemRequest;
import com.freshcart.order_service.data.request.OrderRequest;
import com.freshcart.order_service.data.response.OrderItemResponse;
import com.freshcart.order_service.data.response.OrderResponse;
import com.freshcart.order_service.data.utils.IdName;
import com.freshcart.order_service.entity.delivery.DeliverySlotEntity;
import com.freshcart.order_service.entity.order.OrderEntity;
import com.freshcart.order_service.entity.order.OrderItemEntity;
import com.freshcart.order_service.entity.order.OrderStatus;
import com.freshcart.order_service.entity.product.ProductEntity;
import com.freshcart.order_service.entity.user.UserEntity;
import com.freshcart.order_service.exception.CustomException;
import com.freshcart.order_service.exception.ResourceNotFoundException;
import com.freshcart.order_service.repository.orderRepository.OrderRepository;
import com.freshcart.order_service.repository.productRepository.ProductRepository;
import com.freshcart.order_service.repository.slotRepository.DeliverySlotRepository;
import com.freshcart.order_service.repository.user.UserRepository;
import jakarta.validation.ValidationException;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeliverySlotRepository deliverySlotRepository;

    //<editor-fold desc="Create Order">
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        // IF REQUEST IS NULL OR THERE IS NULL OBJECT OR DUPLICATE PRODUCT -> ERROR
        checkDuplicateProductInorder(request);

        // user = userRepository.findFirstByIdAndDeletedIsFalse(sessionHolder.userId)
        UserEntity fakeUser = userRepository.findFirstByIdAndDeletedIsFalse(1);
        if (fakeUser == null) {
            throw new ResourceNotFoundException("User not found");
        }


        //Check Slot Capacity
        DeliverySlotEntity orderSlot = request.getSlot() == 0 ?
                null : deliverySlotRepository.findSlotForUpdate(request.getSlot());
        // IF ACTIVE ORDER IS EQUAL OR MORE THAN SLOT -> ERROR
        checkOrderSlotValidation(orderSlot);
        checkActiveOrderInSlotWithSlotAvailableOrderNumber(request, orderSlot);


        OrderEntity order = new OrderEntity();
        BigDecimal totalPrice = BigDecimal.ZERO;

        List<OrderItemEntity> orderItems = new ArrayList<>();

        for (var m : request.getOrderItems()) {
            //  ADD ORDER ITEMS AND RETURN TOTAL PRICE OF ALL PRODUCT
            totalPrice = getTotalPriceAndOrderItems(m, totalPrice, orderItems, order);
        }
        if (orderItems.isEmpty()) {
            throw new ValidationException("Basket Is Empty");
        }

        order.setSlot(orderSlot);
        order.setOrderStatus(OrderStatus.ORDER_APPROVED);
        order.setUser(fakeUser);
        order.setTotalPrice(totalPrice);
        order.setOrderItems(orderItems);

        orderRepository.save(order);

        return new OrderResponse(order.getId(), order.getOrderStatus(), totalPrice);
    }

    private void checkOrderSlotValidation(DeliverySlotEntity orderSlot) {
        if (orderSlot == null) {
            throw new ValidationException("Order Slot is Empty");
        }
        LocalDateTime nowDate = LocalDateTime.now();
        if (orderSlot.getEndTime() == null || orderSlot.getStartTime() == null) {
            throw new ValidationException("Order Slot End Time Or start Time is Empty");
        }

        if (!orderSlot.getStartTime().isAfter(nowDate)) {
            throw new ValidationException("you can not order,  because delivering already started ");
        }
        if (!orderSlot.getStartTime().isBefore(orderSlot.getEndTime())) {
            throw new ValidationException("Invalid slot time range");
        }
    }


    /**
     * ADD ORDER ITEMS AND RETURN TOTAL PRICE OF ALL PRODUCT
     *
     * @param m
     * @param totalPrice
     * @param orderItems
     * @param order
     * @return
     */
    private @NonNull BigDecimal getTotalPriceAndOrderItems(OrderItemRequest m, BigDecimal totalPrice, List<OrderItemEntity> orderItems, OrderEntity order) {
        if (m.getQuantity() <= 0) {
            throw new ValidationException("order quantity should be greater than 0");
        }

        ProductEntity product = m.getProduct() == 0
                ? null
                : productRepository.findFirstByIdAndDeletedIsFalse(m.getProduct());

        // REDUCE PRODUCT STOCK
        reduceProductStockCount(m, product);

        totalPrice = totalPrice.add(
                product.getPrice().multiply(BigDecimal.valueOf(m.getQuantity()))
        );

        orderItems.add(new OrderItemEntity(
                order,
                product,
                m.getQuantity(),
                product.getPrice()
        ));
        return totalPrice;
    }

    /**
     * // REDUCE PRODUCT STOCK
     *
     * @param m
     * @param product
     */
    private void reduceProductStockCount(OrderItemRequest m, ProductEntity product) {
        if (product == null) {
            throw new ResourceNotFoundException("No product in order");
        }
        if (product.getStock() < m.getQuantity()) {
            throw new CustomException("Not enough products in stock");
        }

        product.setStock(product.getStock() - m.getQuantity());
    }

    /**
     * // IF ACTIVE ORDER IS EQUAL OR MORE THAN SLOT -> ERROR
     *
     * @param request
     * @param orderSlot
     */
    private void checkActiveOrderInSlotWithSlotAvailableOrderNumber(OrderRequest request, DeliverySlotEntity orderSlot) {
        long activeOrdersInThisSlot = orderRepository.findActiveOrders(request.getSlot(), OrderStatus.CANCELLED);

        if (activeOrdersInThisSlot >= orderSlot.getMaxOrder()) {
            throw new CustomException("Slot is Full");
        }
    }

    /**
     * // IF REQUEST IS NULL OR THERE IS NULL OBJECT OR DUPLICATE PRODUCT -> ERROR
     *
     * @param request
     */
    private static void checkDuplicateProductInorder(OrderRequest request) {
        if (request == null || request.getOrderItems() == null || request.getOrderItems().isEmpty()) {
            throw new ValidationException("Basket Is Empty");
        }

        boolean hasNullItem = request.getOrderItems()
                .stream()
                .anyMatch(item -> item == null);
        if (hasNullItem) {
            throw new ValidationException("Order item cannot be null");
        }
        List<OrderItemRequest> duplicateOrderItems = request.getOrderItems();
        boolean hasDuplicateProducts = duplicateOrderItems.stream()
                .map(OrderItemRequest::getProduct)
                .distinct()
                .count() != duplicateOrderItems.size();
        if (hasDuplicateProducts) {
            throw new ValidationException("Duplicate products in order");
        }
    }
    //</editor-fold>

    //<editor-fold desc="Get Order">

    @Transactional(readOnly = true)
    public OrderResponse getOrder(long id) {
        OrderEntity order = id == 0 ? null
                : orderRepository.findFirstByIdAndDeletedIsFalse(id);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found");
        }
        return new OrderResponse(id,
                new IdName(order.getUser().getId(), order.getUser().getLastName()),
                new IdName(order.getSlot().getId(), order.getSlot().getStartTime().toString() + " - " + order.getSlot().getEndTime().toString()),
                order.getOrderStatus().toString(),
                order.getTotalPrice(),
                order.getCreationDate(),
                order.getOrderItems() == null ? null : order.getOrderItems().stream().map(
                        OrderItemResponse::new).toList()
        );
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrderList() {
        List<OrderEntity> orders = orderRepository.findAllByDeletedIsFalse();
        if (orders == null || orders.size() == 0) {
            return new ArrayList<>();
        }
        return orders.stream().map(
                m -> new OrderResponse(m.getId(),
                        new IdName(m.getUser().getId(), m.getUser().getLastName()),
                        new IdName(m.getSlot().getId(), m.getSlot().getStartTime().toString() + " - " + m.getSlot().getEndTime().toString()),
                        m.getOrderStatus().toString(),
                        m.getTotalPrice(),
                        m.getCreationDate(),
                        m.getOrderItems() == null ? null : m.getOrderItems().stream().map(
                                OrderItemResponse::new).toList()
                )
        ).toList();
    }

    //</editor-fold>


    //<editor-fold desc="Cancel Order">
    @Transactional
    public void deleteOrder(long orderId) {
        OrderEntity order = orderRepository.
                findFirstByIdAndDeletedIsFalseAndOrderStatusNotIn
                        (orderId,
                                new OrderStatus[]{OrderStatus.ON_THE_WAY,
                                        OrderStatus.DELIVERED,
                                        OrderStatus.CANCELLED});
        if (order == null) {
            throw new ResourceNotFoundException("Order not found");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);

        order.getOrderItems().forEach(m -> {
            m.getProduct().setStock(m.getProduct().getStock() + m.getQuantity());
        });
    }
    //</editor-fold>
}
