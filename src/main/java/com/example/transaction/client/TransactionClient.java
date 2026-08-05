package com.example.transaction.client;

import com.example.transaction.dto.TransactionResponseDto;

public interface TransactionClient {

    /**
     * Retrieves the transaction record identified by the given transaction ID from the
     * external Serviex service. Implementations must surface any HTTP 409 (Conflict)
     * responses so that the global exception handler can translate them to HTTP 500.
     *
     * @param idTransaction the external transaction identifier used as a path variable
     *                      when invoking the Serviex API; must not be {@code null} or blank
     * @return a {@link com.example.transaction.dto.TransactionResponseDto} containing the
     *         amount, store name, currency code, and the list of associated users returned
     *         by Serviex
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException.Conflict
     *         if the Serviex API responds with HTTP 409; caught and remapped to HTTP 500
     *         by the global exception handler
     */
    TransactionResponseDto getTransaction(String idTransaction);
}
