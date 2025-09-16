package com.innowise.OrderService.dto.item;

import lombok.Data;

@Data
public class ItemResponseDto {

    private Long id;

    private String name;

    private double price;
}
