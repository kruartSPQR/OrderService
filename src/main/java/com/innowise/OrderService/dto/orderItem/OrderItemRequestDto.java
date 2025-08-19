package com.innowise.OrderService.dto.orderItem;

import lombok.Data;

@Data
public class OrderItemRequestDto {
    private Long itemId;
    private Integer quantity;
}
