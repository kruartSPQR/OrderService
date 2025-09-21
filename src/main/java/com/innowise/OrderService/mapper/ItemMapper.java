package com.innowise.OrderService.mapper;

import com.innowise.OrderService.dto.item.ItemRequestDto;
import com.innowise.OrderService.dto.item.ItemResponseDto;
import com.innowise.OrderService.dto.orderItem.OrderItemResponseDto;
import com.innowise.OrderService.entity.Item;
import com.innowise.OrderService.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {


    @Mapping(target = "id", ignore = true)
    Item toEntity(ItemRequestDto itemRequestDto);

    ItemResponseDto toDto(Item item);

    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "item.name", target = "itemName")
    @Mapping(source = "item.price", target = "price")
    OrderItemResponseDto orderItemToDto(OrderItem orderItem);
}
