package com.sergejs.orderservice.controller;

import com.sergejs.orderservice.dto.request.OrderRequest;
import com.sergejs.orderservice.dto.response.OrderCreatedResponse;
import com.sergejs.orderservice.dto.response.OrderResponse;
import com.sergejs.orderservice.dto.response.TotalPriceResponse;
import com.sergejs.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(@RequestHeader("user-id") UUID userId) {
        return ResponseEntity.ok(orderService.getAllOrders(userId));
    }

    @GetMapping("/get-total-price")
    public ResponseEntity<TotalPriceResponse> getTotalPriceWithinRange(@RequestHeader("user-id") UUID userId,
                                                                       @RequestParam("fromDate") LocalDate fromDate,
                                                                       @RequestParam("toDate") LocalDate toDate) {
        return ResponseEntity.ok(orderService.getTotalPriceWithinRange(fromDate, toDate, userId));
    }

    @PostMapping
    public ResponseEntity<OrderCreatedResponse> createOrder(@RequestHeader("user-id") UUID userId,
                                                            @RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(orderService.save(userId, orderRequest));
    }

}
