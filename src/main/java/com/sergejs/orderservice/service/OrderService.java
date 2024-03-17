package com.sergejs.orderservice.service;

import com.sergejs.orderservice.dto.request.OrderRequest;
import com.sergejs.orderservice.dto.response.OrderCreatedResponse;
import com.sergejs.orderservice.dto.response.OrderResponse;
import com.sergejs.orderservice.dto.response.TotalPriceResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    /**
     * GEt all the orders for the user provided.
     *
     * @param userId corresponding user ID.
     * @return all the orders for that particular user.
     */
    List<OrderResponse> getAllOrders(UUID userId);

    /**
     * Get total price within the specific range for specific user.
     *
     * @param fromDate lower date bound.
     * @param toDate   higher date bound.
     * @param userId   user whose orders have to be taken into account.
     * @return the total price response.
     */
    TotalPriceResponse getTotalPriceWithinRange(LocalDate fromDate, LocalDate toDate, UUID userId);

    /**
     * Save an order for a given user.
     *
     * @param userId  user for whom an order has to be created.
     * @param request order creation request.
     * @return created oder identifier
     */
    OrderCreatedResponse save(UUID userId, OrderRequest request);

}
