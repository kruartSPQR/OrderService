package com.innowise.OrderService.dto.item;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemResponseDto {

    private Long id;

    private String name;

    private double price;
}
