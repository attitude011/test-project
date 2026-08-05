package com.example.transaction.service;

import com.example.transaction.dto.TransactionResponseDto;

public interface TransactionService {

    /**
     * Retrieves the transaction record for the given identifier by delegating to the
     * client layer, which calls the external Serviex service.
     *
     * @param idTransaction the Serviex transaction identifier used as a path variable in
     *                      the downstream HTTP call; must not be {@code null} or blank
     * @return a {@link com.example.transaction.dto.TransactionResponseDto} containing the
     *         amount, store name, currency code, and the list of associated users as
     *         returned by Serviex
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException.Conflict
     *         propagated from the client layer when Serviex responds with HTTP 409; handled
     *         globally by {@link com.example.transaction.exception.GlobalExceptionHandler}
     */
    TransactionResponseDto getTransaction(String idTransaction);
}
