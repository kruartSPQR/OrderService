package com.innowise.OrderService.mapper;

import com.innowise.OrderService.dto.order.OrderRequestDto;
import com.innowise.OrderService.dto.order.OrderResponseDto;
import com.innowise.OrderService.entity.Order;
import com.innowise.OrderService.entity.OrderItem;
import com.innowise.OrderService.dto.orderItem.OrderItemResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order toEntity(OrderRequestDto orderRequestDto);

    OrderResponseDto toDto(Order order);

    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "item.name", target = "itemName")
    @Mapping(source = "item.price", target = "price")
    OrderItemResponseDto orderItemToDto(OrderItem orderItem);
}
