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
     * Constructs a new {@code WebClientTransactionClient} and initialises the underlying
     * {@link WebClient} from the application-scoped {@link WebClient.Builder}.
     *
     * @param webClientBuilder the Spring-managed {@link WebClient.Builder} used to create
     *                         the reactive HTTP client; must not be {@code null}
     */
    public WebClientTransactionClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Performs a blocking {@code GET} request to the Serviex external service. The full
     * URI is assembled at runtime as {@code <serviex.base-url><serviex.transactions-path>/{id}},
     * where both prefix segments are injected from {@code application.yml} and the
     * {@code id} path variable is substituted with the supplied {@code idTransaction}.
     *
     * <p>A Serviex HTTP 409 response is <em>not</em> caught here; it is deliberately
     * allowed to propagate as a
     * {@link org.springframework.web.reactive.function.client.WebClientResponseException.Conflict}
     * so that the global {@link com.example.transaction.exception.GlobalExceptionHandler}
     * can translate it into a standardised HTTP 500 payload.
     *
     * @param idTransaction the Serviex transaction identifier used as the {@code {id}} path
     *                      variable; must not be {@code null} or blank
     * @return a {@link com.example.transaction.dto.TransactionResponseDto} deserialised from
     *         the JSON body returned by Serviex, containing amount, store, currency, and users
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException
     *         for any non-2xx HTTP response; callers should treat 409 specifically via the
     *         global exception handler
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
