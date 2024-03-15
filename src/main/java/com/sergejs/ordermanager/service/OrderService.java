package com.sergejs.ordermanager.service;

import com.sergejs.ordermanager.dto.request.OrderRequest;
import com.sergejs.ordermanager.dto.response.OrderCreatedResponse;
import com.sergejs.ordermanager.dto.response.OrderResponse;
import com.sergejs.ordermanager.dto.response.TotalPriceResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    List<OrderResponse> getAllOrders(UUID userId);

    TotalPriceResponse getTotalPriceWithinRange(LocalDate fromDate, LocalDate toDate, UUID userId);

    OrderCreatedResponse save(UUID userId, OrderRequest request);

}
