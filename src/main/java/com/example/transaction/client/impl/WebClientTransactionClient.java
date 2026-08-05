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

    /**
     * @param webClientBuilder Spring-managed builder; must not be {@code null}
     */
    public WebClientTransactionClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Calls Serviex at {@code <serviex.base-url><serviex.transactions-path>/{id}}.
     * HTTP 409 is not caught here — it propagates for the global exception handler.
     *
     * @param idTransaction path variable forwarded to Serviex; must not be {@code null}
     * @return populated {@link TransactionResponseDto}
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException
     *         on any non-2xx response
     */
    @Override
    public TransactionResponseDto getTransaction(String idTransaction) {
        return webClient.get()
                .uri(baseUrl + transactionsPath + "/{id}", idTransaction)
                .retrieve()
                .bodyToMono(TransactionResponseDto.class)
                .block();
    }
}
