package com.example.transaction.client;

import com.example.transaction.dto.TransactionResponseDto;

public interface TransactionClient {

    /**
     * Retrieves the transaction record for the given ID from Serviex.
     *
     * @param idTransaction Serviex transaction identifier; must not be {@code null}
     * @return populated {@link com.example.transaction.dto.TransactionResponseDto}
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException.Conflict
     *         when Serviex returns HTTP 409; remapped to HTTP 500 by the global exception handler
     */
    TransactionResponseDto getTransaction(String idTransaction);
}
