package com.sergejs.ordermanager.mapper;

import com.sergejs.ordermanager.dto.request.OrderRequest;
import com.sergejs.ordermanager.dto.response.OrderResponse;
import com.sergejs.ordermanager.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    Order map(OrderRequest orderRequest);

    OrderResponse map(Order order);

    List<OrderResponse> map(List<Order> order);

}
