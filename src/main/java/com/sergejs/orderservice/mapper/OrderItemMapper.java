package com.sergejs.orderservice.mapper;

import com.sergejs.orderservice.dto.request.OrderItemRequest;
import com.sergejs.orderservice.model.OrderItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    List<OrderItem> map(List<OrderItemRequest> orderItemRequests);

}
