package com.example.transaction.client.impl;

import com.example.transaction.client.TransactionClient;
import com.example.transaction.dto.TransactionResponseDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WebClientTransactionClient implements TransactionClient {

    private final WebClient webClient;

    @Value("${serviex.base-url}")
    private String baseUrl;

    @Value("${serviex.transactions-path}")
    private String transactionsPath;

    public WebClientTransactionClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    @Override
    public TransactionResponseDto getTransaction(String idTransaction) {
        return webClient.get()
                .uri(baseUrl + transactionsPath + "/{id}", idTransaction)
                .retrieve()
                .bodyToMono(TransactionResponseDto.class)
                .block();
    }
}
