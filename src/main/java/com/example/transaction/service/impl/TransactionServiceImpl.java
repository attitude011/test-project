package com.example.transaction.service.impl;

import com.example.transaction.client.TransactionClient;
import com.example.transaction.dto.TransactionResponseDto;
import com.example.transaction.service.TransactionService;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionClient transactionClient;

    /**
     * @param transactionClient HTTP client for the Serviex API; must not be {@code null}
     */
    public TransactionServiceImpl(TransactionClient transactionClient) {
        this.transactionClient = transactionClient;
    }

    /**
     * Delegates directly to {@link TransactionClient#getTransaction(String)}.
     *
     * @param idTransaction Serviex transaction identifier; must not be {@code null}
     * @return populated {@link TransactionResponseDto}
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException.Conflict
     *         when Serviex returns HTTP 409
     */
    @Override
    public TransactionResponseDto getTransaction(String idTransaction) {
        return transactionClient.getTransaction(idTransaction);
    }
}
