package com.innowise.OrderService.dto.orderItem;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemRequestDto {
    private Long itemId;
    private Integer quantity;
}
