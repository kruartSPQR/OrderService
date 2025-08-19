package com.innowise.OrderService.dto.orderItem;

import lombok.Data;

@Data
public class OrderItemResponseDto {

    private Long itemId;
    private String itemName;
    private Integer quantity;
    private Long price;
}
