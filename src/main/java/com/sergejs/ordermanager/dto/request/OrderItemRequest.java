package com.sergejs.ordermanager.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class OrderItemRequest {

    private UUID itemId;
    private String itemName;
    private BigDecimal unitPrice;
    private Integer quantity;

}
