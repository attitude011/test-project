package com.example.transaction.apiclient;

import com.example.transaction.apiclient.exception.ApiClientException;
import com.example.transaction.dto.TransactionResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Typed HTTP client for {@code GET /getTrx/{id}} (JWT-protected).
 */
@Component
public class TransactionApiClient {

    private static final String GET_TRX_PATH = "/getTrx/{id}";

    private final WebClient webClient;

    /**
     * @param webClient local API {@link WebClient} qualified as {@code "localApiWebClient"}
     */
    public TransactionApiClient(@Qualifier("localApiWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Calls {@code GET /getTrx/{id}} with a {@code Bearer} token.
     *
     * @param id          transaction identifier path variable; must not be {@code null}
     * @param bearerToken valid JWT (without {@code "Bearer "} prefix); must not be {@code null}
     * @return populated {@link TransactionResponseDto}
     * @throws ApiClientException on HTTP 401 (invalid token), HTTP 500 (upstream conflict),
     *         or any transport failure
     */
    public TransactionResponseDto getTransaction(String id, String bearerToken) {
        try {
            return webClient.get()
                    .uri(GET_TRX_PATH, id)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                    .retrieve()
                    .bodyToMono(TransactionResponseDto.class)
                    .block();
        } catch (Exception e) {
            throw new ApiClientException(
                    "Failed to call GET /getTrx/" + id + ": " + e.getMessage(), e);
        }
    }
}
