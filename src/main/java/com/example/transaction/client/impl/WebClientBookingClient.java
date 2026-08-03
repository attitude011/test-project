package com.example.transaction.client.impl;

import com.example.transaction.dto.BookingResponse;
import com.example.transaction.exception.BookingApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @deprecated Superseded by {@link BookingWebClientImpl}.
 *             Kept to avoid a compile-breaking file removal; no longer a Spring bean.
 */
@Deprecated
public class WebClientBookingClient {

    private final WebClient webClient;

    @Value("${booking.api.url}")
    private String apiUrl;

    @Value("${booking.error.message}")
    private String errorMessage;

    public WebClientBookingClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

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
