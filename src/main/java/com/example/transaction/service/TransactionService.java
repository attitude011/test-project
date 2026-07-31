package com.example.transaction.service;

import com.example.transaction.dto.TransactionResponseDto;

public interface TransactionService {
    TransactionResponseDto getTransaction(String idTransaction);
}
