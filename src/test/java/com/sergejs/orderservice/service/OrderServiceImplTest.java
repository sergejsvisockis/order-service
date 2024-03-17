package com.sergejs.orderservice.service;

import com.sergejs.orderservice.dto.request.OrderItemRequest;
import com.sergejs.orderservice.dto.request.OrderRequest;
import com.sergejs.orderservice.dto.response.OrderResponse;
import com.sergejs.orderservice.mapper.OrderMapper;
import com.sergejs.orderservice.model.Order;
import com.sergejs.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldGetAllOrders() {
        UUID userId = UUID.randomUUID();
        Order order = new Order();
        List<Order> orderList = List.of(order);
        when(orderRepository.findAllOrdersByUserId(eq(userId))).thenReturn(orderList);
        when(orderMapper.map(eq(orderList))).thenReturn(List.of(new OrderResponse(LocalDate.now(), List.of())));

        List<OrderResponse> allOrders = orderService.getAllOrders(userId);

        assertEquals(1, allOrders.size());
        verify(orderRepository).findAllOrdersByUserId(userId);
        verify(orderMapper).map(orderList);
    }

    @Test
    void shouldCalculateTenPercentDiscount() {
        OrderRequest request = new OrderRequest(List.of(
                OrderItemRequest.builder()
                        .itemId(UUID.randomUUID())
                        .itemName("someItemOne")
                        .unitPrice(BigDecimal.valueOf(300))
                        .quantity(7)
                        .build(),
                OrderItemRequest.builder()
                        .itemId(UUID.randomUUID())
                        .itemName("someItemTwo")
                        .unitPrice(BigDecimal.valueOf(500))
                        .quantity(6)
                        .build(),
                OrderItemRequest.builder()
                        .itemId(UUID.randomUUID())
                        .itemName("someItemThree")
                        .unitPrice(BigDecimal.valueOf(350))
                        .quantity(8)
                        .build()
        ));

        List<OrderItemRequest> orderItemRequests = orderService.calculateDiscount(request);

        assertEquals(270.0, orderItemRequests.get(0).getUnitPrice().intValue());
        assertEquals(315.0, orderItemRequests.get(1).getUnitPrice().intValue());
        assertEquals(450.0, orderItemRequests.get(2).getUnitPrice().intValue());
    }

    @Test
    void shouldCalculateFifteenPercentDiscount() {
        OrderRequest request = new OrderRequest(List.of(
                OrderItemRequest.builder()
                        .itemId(UUID.randomUUID())
                        .itemName("someItemOne")
                        .unitPrice(BigDecimal.valueOf(300))
                        .quantity(13)
                        .build()
        ));

        List<OrderItemRequest> orderItemRequests = orderService.calculateDiscount(request);

        assertEquals(255, orderItemRequests.get(0).getUnitPrice().intValue());
    }
}