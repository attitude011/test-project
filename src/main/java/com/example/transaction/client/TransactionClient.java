package com.example.transaction.client;

import com.example.transaction.dto.TransactionResponseDto;

public interface TransactionClient {
    TransactionResponseDto getTransaction(String idTransaction);
}
