package com.innowise.OrderService.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemRequestDto {

    @NotBlank
    @Size(max = 255)
    private String name;

    @PositiveOrZero
    private double price;

}
