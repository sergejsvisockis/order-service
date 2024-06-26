package com.sergejs.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TotalPriceResponse {

    private BigDecimal totalPrice;

}
