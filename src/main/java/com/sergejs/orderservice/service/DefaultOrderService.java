package com.sergejs.orderservice.service;

import com.sergejs.orderservice.dto.request.OrderItemRequest;
import com.sergejs.orderservice.dto.request.OrderRequest;
import com.sergejs.orderservice.dto.response.OrderCreatedResponse;
import com.sergejs.orderservice.dto.response.OrderResponse;
import com.sergejs.orderservice.dto.response.TotalPriceResponse;
import com.sergejs.orderservice.mapper.OrderItemMapper;
import com.sergejs.orderservice.mapper.OrderMapper;
import com.sergejs.orderservice.model.Order;
import com.sergejs.orderservice.model.OrderItem;
import com.sergejs.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DefaultOrderService implements OrderService {

    private static final Double DISCOUNT_TEN_PERCENT = 0.10;
    private static final Double DISCOUNT_FIFTEEN_PERCENT = 0.15;

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;

    @Override
    public List<OrderResponse> getAllOrders(UUID userId) {
        return orderMapper.map(orderRepository.findAllOrdersByUserId(userId));
    }

    @Override
    public TotalPriceResponse getTotalPriceWithinRange(LocalDate fromDate, LocalDate toDate, UUID userId) {
        return new TotalPriceResponse(orderRepository.getTotalPriceWithinRange(fromDate, toDate, userId));
    }

    @Override
    @Transactional
    public OrderCreatedResponse save(UUID userId, OrderRequest request) {
        List<OrderItemRequest> modifiedItems = calculateDiscount(request);

        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setUserId(userId);
        List<OrderItem> orderItems = orderItemMapper.map(modifiedItems);
        order.setOrderItems(orderItems);
        order.getOrderItems().forEach(item -> item.setOrder(order));

        Order savedOrder = orderRepository.save(order);
        return new OrderCreatedResponse(savedOrder.getId());
    }

    protected List<OrderItemRequest> calculateDiscount(OrderRequest request) {
        return request.getOrderItems()
                .stream()
                .collect(Collectors.groupingBy(OrderItemRequest::getItemName))
                .values()
                .stream()
                .flatMap(this::calculateDiscount)
                .toList();
    }

    private Stream<OrderItemRequest> calculateDiscount(List<OrderItemRequest> items) {
        int numberOfTheSameItems = getNumberOfItemsOfTheSameName(items);
        if (numberOfTheSameItems >= 5 && numberOfTheSameItems <= 10) {
            calculateDiscount(items, DISCOUNT_TEN_PERCENT);
        }
        if (numberOfTheSameItems > 10) {
            calculateDiscount(items, DISCOUNT_FIFTEEN_PERCENT);
        }
        return items.stream();
    }

    private void calculateDiscount(List<OrderItemRequest> items, Double discount) {
        items.forEach(item -> {
                    BigDecimal unitPrice = item.getUnitPrice();
                    item.setUnitPrice(unitPrice.subtract(unitPrice
                            .multiply(BigDecimal.valueOf(discount))));
                }
        );
    }

    private Integer getNumberOfItemsOfTheSameName(List<OrderItemRequest> items) {
        return items.stream()
                .mapToInt(OrderItemRequest::getQuantity)
                .sum();
    }
}
