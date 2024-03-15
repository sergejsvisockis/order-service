package com.sergejs.ordermanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderResponse {

    private LocalDate orderDate;
    private List<OrderItemResponse> orderItems;

}
