package com.sergejs.ordermanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class OrderItemResponse {

    private UUID itemId;
    private String itemName;
    private BigDecimal unitPrice;
    private Integer quantity;

}
