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
     * @param transactionService service delegate; must not be {@code null}
     */
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * {@code GET /getTrx/{id}} — JWT-protected; returns the Serviex transaction for the given ID.
     *
     * @param id transaction identifier path variable
     * @return populated {@link com.example.transaction.dto.TransactionResponseDto}
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException.Conflict
     *         when Serviex returns HTTP 409; remapped to HTTP 500 by the global exception handler
     */
    @GetMapping("/getTrx/{id}")
    public TransactionResponseDto getTransaction(@PathVariable("id") String id) {
        return transactionService.getTransaction(id);
    }
}
