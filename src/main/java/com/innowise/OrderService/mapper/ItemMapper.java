package com.innowise.OrderService.mapper;

import com.innowise.OrderService.dto.item.ItemRequestDto;
import com.innowise.OrderService.dto.item.ItemResponseDto;
import com.innowise.OrderService.entity.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item toEntity(ItemRequestDto itemRequestDto);

    ItemResponseDto toDto(Item item);

}
