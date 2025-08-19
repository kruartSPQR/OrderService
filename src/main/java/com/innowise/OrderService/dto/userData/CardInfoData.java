package com.innowise.OrderService.dto.userData;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CardInfoData {

    private Long id;

    private String number;

    private String holder;

    private LocalDate expirationDate;

    private Long userId;
}
