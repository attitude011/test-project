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
     * Constructs a new {@code BookingWebClientImpl} and initialises the underlying
     * {@link WebClient} from the application-scoped {@link WebClient.Builder}.
     *
     * @param webClientBuilder the Spring-managed {@link WebClient.Builder} used to create
     *                         the reactive HTTP client; must not be {@code null}
     */
    public BookingWebClientImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Performs a blocking {@code GET} request to the external Restful-Booker API using the
     * URL template configured under {@code booking.api.url} in {@code application.yml},
     * substituting the supplied {@code id} as the {@code {id}} path variable.
     *
     * <p>If the HTTP call fails for any reason — including network timeouts, non-2xx responses,
     * or JSON deserialization errors — the originating exception is wrapped in a
     * {@link com.example.transaction.exception.BookingApiException} whose message is drawn
     * from the {@code booking.error.message} property. This ensures a controlled 500 error
     * surface via the global exception handler.
     *
     * @param id the unique booking identifier to append as a path variable; must not be
     *           {@code null}
     * @return a fully-populated {@link com.example.transaction.dto.BookingResponse} with
     *         all fields as returned by the external API
     * @throws com.example.transaction.exception.BookingApiException wrapping any exception
     *         thrown during the HTTP call; the message is the value of
     *         {@code booking.error.message} from {@code application.yml}
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

