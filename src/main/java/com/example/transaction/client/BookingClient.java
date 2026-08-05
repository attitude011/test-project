package com.example.transaction.client;

import com.example.transaction.dto.BookingResponse;

public interface BookingClient {

    /**
     * Fetches the full booking record for the given booking identifier from the external
     * Restful-Booker API. Implementations are responsible for translating any HTTP-level
     * failure into a domain exception.
     *
     * @param id the unique numeric booking identifier as understood by the external API;
     *           must not be {@code null}
     * @return a fully-populated {@link com.example.transaction.dto.BookingResponse} containing
     *         all fields returned by the external API (firstname, lastname, totalprice,
     *         depositpaid, bookingdates, additionalneeds)
     * @throws com.example.transaction.exception.BookingApiException if the external API call
     *         fails for any reason (network error, non-2xx response, deserialization failure)
     */
    BookingResponse getBooking(Integer id);
}

