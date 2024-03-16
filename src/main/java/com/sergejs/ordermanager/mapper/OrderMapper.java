package com.sergejs.ordermanager.mapper;

import com.sergejs.ordermanager.dto.response.OrderResponse;
import com.sergejs.ordermanager.model.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse map(Order order);

    List<OrderResponse> map(List<Order> order);

}
