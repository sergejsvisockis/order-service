package com.sergejs.ordermanager.mapper;

import com.sergejs.ordermanager.dto.request.OrderItemRequest;
import com.sergejs.ordermanager.model.OrderItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    List<OrderItem> map(List<OrderItemRequest> orderItemRequests);

}
