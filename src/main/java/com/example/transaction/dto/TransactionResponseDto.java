package com.example.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDto {
    private int amount;
    private String store;
    private String currency;
    private List<UserDto> users;
}
