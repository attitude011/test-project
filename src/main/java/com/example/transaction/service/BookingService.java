package com.example.transaction.service;

import com.example.transaction.dto.BookingResponse;

public interface BookingService {

    /**
     * Retrieves the booking from the external API, persists it to MongoDB, and returns it.
     * DB failures are silently swallowed — the API flow is never interrupted.
     *
     * @param id booking identifier; must not be {@code null}
     * @return populated {@link com.example.transaction.dto.BookingResponse}
     * @throws com.example.transaction.exception.BookingApiException on external API failure
     */
    BookingResponse getBooking(Integer id);
}
