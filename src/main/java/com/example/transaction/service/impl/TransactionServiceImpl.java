package com.example.transaction.service.impl;

import com.example.transaction.client.TransactionClient;
import com.example.transaction.dto.TransactionResponseDto;
import com.example.transaction.service.TransactionService;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionClient transactionClient;

    public TransactionServiceImpl(TransactionClient transactionClient) {
        this.transactionClient = transactionClient;
    }

    @Override
    public TransactionResponseDto getTransaction(String idTransaction) {
        return transactionClient.getTransaction(idTransaction);
    }
}
