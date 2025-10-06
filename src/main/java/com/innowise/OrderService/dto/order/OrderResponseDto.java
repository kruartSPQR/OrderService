package com.innowise.OrderService.dto.order;

import com.innowise.OrderService.dto.orderItem.OrderItemResponseDto;
import com.innowise.OrderService.dto.userData.UserData;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
public class OrderResponseDto {
    private Long id;

    private String userId;

    private String status;

    private LocalDateTime creationDate;

    private UserData userInfo;

    private List<OrderItemResponseDto> orderItems;
}
