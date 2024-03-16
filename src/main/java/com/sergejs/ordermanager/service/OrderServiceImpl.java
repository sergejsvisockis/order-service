package com.sergejs.ordermanager.service;

import com.sergejs.ordermanager.dto.request.OrderItemRequest;
import com.sergejs.ordermanager.dto.request.OrderRequest;
import com.sergejs.ordermanager.dto.response.OrderCreatedResponse;
import com.sergejs.ordermanager.dto.response.OrderResponse;
import com.sergejs.ordermanager.dto.response.TotalPriceResponse;
import com.sergejs.ordermanager.mapper.OrderItemMapper;
import com.sergejs.ordermanager.mapper.OrderMapper;
import com.sergejs.ordermanager.model.Order;
import com.sergejs.ordermanager.model.OrderItem;
import com.sergejs.ordermanager.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Double DISCOUNT_TEN_PERCENT = 0.10;
    private static final Double DISCOUNT_FIFTEEN_PERCENT = 0.10;

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

    private List<OrderItemRequest> calculateDiscount(OrderRequest request) {
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
        items.forEach(item -> item.setUnitPrice(item.getUnitPrice()
                .subtract(item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(discount))))
        );
    }

    private static int getNumberOfItemsOfTheSameName(List<OrderItemRequest> group) {
        return group.stream()
                .mapToInt(OrderItemRequest::getQuantity)
                .sum();
    }
}
