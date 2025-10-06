package com.innowise.OrderService.dto.order;

import com.innowise.OrderService.dto.orderItem.OrderItemRequestDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class OrderUpdateRequestDto {

    @NotNull
    @Email
    String userId;

    @NotEmpty
    private List<OrderItemRequestDto> items;
}
