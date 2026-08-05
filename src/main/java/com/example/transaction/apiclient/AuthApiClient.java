package com.example.transaction.apiclient;

import com.example.transaction.apiclient.exception.ApiClientException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Typed HTTP client for {@code GET /generate-token}.
 */
@Component
public class AuthApiClient {

    private static final String GENERATE_TOKEN_PATH = "/generate-token";

    private final WebClient webClient;

    /**
     * @param webClient local API {@link WebClient} qualified as {@code "localApiWebClient"}
     */
    public AuthApiClient(@Qualifier("localApiWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Calls {@code GET /generate-token} and returns a 1-hour JWT for local/Postman testing.
     *
     * @return compact JWT string; never {@code null} on success
     * @throws ApiClientException on HTTP or transport failure
     */
    public String generateToken() {
        try {
            return webClient.get()
                    .uri(GENERATE_TOKEN_PATH)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            throw new ApiClientException(
                    "Failed to call GET " + GENERATE_TOKEN_PATH + ": " + e.getMessage(), e);
        }
    }
}
