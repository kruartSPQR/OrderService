package com.innowise.OrderService.dto.userData;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserData {

    private Long id;

    private String name;

    private String surname;

    private String email;

    private LocalDate birthDate;

    private List<CardInfoData> cardList;
}
