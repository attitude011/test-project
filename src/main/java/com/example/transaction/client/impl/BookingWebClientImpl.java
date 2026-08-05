package com.example.transaction.client.impl;

import com.example.transaction.client.BookingClient;
import com.example.transaction.dto.BookingResponse;
import com.example.transaction.exception.BookingApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BookingWebClientImpl implements BookingClient {

    private final WebClient webClient;

    @Value("${booking.api.url}")
    private String apiUrl;

    @Value("${booking.error.message}")
    private String errorMessage;

    /**
     * @param webClientBuilder Spring-managed builder; must not be {@code null}
     */
    public BookingWebClientImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Calls the external Restful-Booker API at the URL from {@code booking.api.url}.
     * Any failure is wrapped in {@link com.example.transaction.exception.BookingApiException}
     * using the message from {@code booking.error.message}.
     *
     * @param id booking identifier path variable; must not be {@code null}
     * @return populated {@link BookingResponse}
     * @throws com.example.transaction.exception.BookingApiException on any HTTP or transport failure
     */
    @Override
    public BookingResponse getBooking(Integer id) {
        try {
            return webClient.get()
                    .uri(apiUrl, id)
                    .retrieve()
                    .bodyToMono(BookingResponse.class)
                    .block();
        } catch (Exception e) {
            throw new BookingApiException(errorMessage, e);
        }
    }
}
