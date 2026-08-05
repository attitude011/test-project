package com.example.transaction.service;

import com.example.transaction.dto.TransactionResponseDto;

public interface TransactionService {

    /**
     * Retrieves the transaction record for the given ID by delegating to the client layer.
     *
     * @param idTransaction Serviex transaction identifier; must not be {@code null}
     * @return populated {@link com.example.transaction.dto.TransactionResponseDto}
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException.Conflict
     *         when Serviex returns HTTP 409
     */
    TransactionResponseDto getTransaction(String idTransaction);
}
