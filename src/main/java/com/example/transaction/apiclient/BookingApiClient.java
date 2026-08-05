package com.example.transaction.apiclient;

import com.example.transaction.apiclient.exception.ApiClientException;
import com.example.transaction.dto.BookingResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Typed HTTP client for {@code GET /booking/{id}} (public endpoint).
 */
@Component
public class BookingApiClient {

    private static final String GET_BOOKING_PATH = "/booking/{id}";

    private final WebClient webClient;

    /**
     * @param webClient local API {@link WebClient} qualified as {@code "localApiWebClient"}
     */
    public BookingApiClient(@Qualifier("localApiWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Calls {@code GET /booking/{id}} and returns the full booking record.
     *
     * @param id numeric booking identifier; must not be {@code null}
     * @return populated {@link BookingResponse}
     * @throws ApiClientException on HTTP 500 (upstream failure) or any transport error
     */
    public BookingResponse getBooking(Integer id) {
        try {
            return webClient.get()
                    .uri(GET_BOOKING_PATH, id)
                    .retrieve()
                    .bodyToMono(BookingResponse.class)
                    .block();
        } catch (Exception e) {
            throw new ApiClientException(
                    "Failed to call GET /booking/" + id + ": " + e.getMessage(), e);
        }
    }
}
