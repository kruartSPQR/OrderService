package com.innowise.OrderService.dto.order;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UpdatePayedOrderStatusRequestDto {

    @NotNull
    @Email
    String userId;

    @Size(max = 32, message = "Status max length is 32")
    String status;

}
