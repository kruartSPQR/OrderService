package com.innowise.OrderService.dto.order;

import com.innowise.OrderService.dto.orderItem.OrderItemRequestDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class OrderUpdateRequestDto {

    @NotNull
    Long userId;

    @Size(max = 32, message = "Status max length is 255")
    String status;

    @NotEmpty
    private List<OrderItemRequestDto> items;
}
