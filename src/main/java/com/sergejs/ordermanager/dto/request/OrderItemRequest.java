package com.sergejs.ordermanager.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@ToString
@Getter
@Setter
@Builder
public class OrderItemRequest {

    private UUID itemId;
    private String itemName;
    private BigDecimal unitPrice;
    private Integer quantity;

}
