package com.example.transaction.service.impl;

import com.example.transaction.client.TransactionClient;
import com.example.transaction.dto.TransactionResponseDto;
import com.example.transaction.service.TransactionService;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionClient transactionClient;

    /**
     * Constructs a new {@code TransactionServiceImpl} with the required client collaborator.
     *
     * @param transactionClient the client layer responsible for HTTP communication with the
     *                          external Serviex service; must not be {@code null}
     */
    public TransactionServiceImpl(TransactionClient transactionClient) {
        this.transactionClient = transactionClient;
    }

    /**
     * Retrieves the transaction record for the given identifier by delegating directly to
     * the {@link com.example.transaction.client.TransactionClient}. This layer applies no
     * transformation; it exists solely to enforce the separation-of-concerns contract
     * between the controller and the HTTP client.
     *
     * @param idTransaction the Serviex transaction identifier forwarded as-is to the client;
     *                      must not be {@code null} or blank
     * @return the {@link com.example.transaction.dto.TransactionResponseDto} returned by the
     *         client layer, containing amount, store, currency, and users
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException.Conflict
     *         propagated from the client when Serviex responds with HTTP 409
     */
    @Override
    public TransactionResponseDto getTransaction(String idTransaction) {
        return transactionClient.getTransaction(idTransaction);
    }
}
