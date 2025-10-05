package com.innowise.OrderService.dto.order;

import com.innowise.OrderService.dto.orderItem.OrderItemRequestDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {

    @NotNull
    @Email
    String userId;

    @NotEmpty
    private List<OrderItemRequestDto> items;
}
