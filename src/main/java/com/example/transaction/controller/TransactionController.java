package com.example.transaction.controller;

import com.example.transaction.dto.TransactionResponseDto;
import com.example.transaction.service.TransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    private final TransactionService transactionService;  

    /**
     * Constructs a new {@code TransactionController} with the provided transaction service.
     *
     * @param transactionService the service layer responsible for orchestrating the
     *                           transaction lookup; must not be {@code null}
     */
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Handles {@code GET /getTrx/{id}} requests by delegating to the transaction service layer.
     *
     * <p>This endpoint requires a valid JWT bearer token in the {@code Authorization} header.
     * The supplied {@code id} is passed through the service and client layers as the Serviex
     * transaction identifier. If Serviex returns HTTP 409, the global exception handler
     * translates it to HTTP 500.
     *
     * @param id the transaction identifier supplied as a URI path variable, forwarded as-is
     *           to the Serviex integration; must not be {@code null} or blank
     * @return a {@link com.example.transaction.dto.TransactionResponseDto} containing
     *         the amount, store name, currency code, and list of associated users
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException.Conflict
     *         propagated from the client layer when Serviex returns HTTP 409; remapped to
     *         HTTP 500 by the global exception handler
     */
    @GetMapping("/getTrx/{id}")
    public TransactionResponseDto getTransaction(@PathVariable("id") String id) {
        return transactionService.getTransaction(id);
    }
}
