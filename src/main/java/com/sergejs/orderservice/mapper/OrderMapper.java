package com.sergejs.orderservice.mapper;

import com.sergejs.orderservice.dto.response.OrderResponse;
import com.sergejs.orderservice.model.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse map(Order order);

    List<OrderResponse> map(List<Order> order);

}
