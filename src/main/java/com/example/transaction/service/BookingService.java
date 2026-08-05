package com.example.transaction.service;

import com.example.transaction.dto.BookingResponse;

public interface BookingService {

    /**
     * Retrieves the booking record for the given identifier, persists it to MongoDB for
     * auditing purposes, and returns the original API response to the caller.
     *
     * <p>The persistence step is <em>resilient</em>: if the database is unavailable the
     * error is logged and the method still returns the {@link com.example.transaction.dto.BookingResponse}
     * to ensure the API flow is never interrupted by a database outage.
     *
     * @param id the unique numeric booking identifier as supplied by the external
     *           Restful-Booker API; must not be {@code null}
     * @return the fully-populated {@link com.example.transaction.dto.BookingResponse}
     *         fetched from the external API; never {@code null} on success
     * @throws com.example.transaction.exception.BookingApiException if the external API
     *         call fails; database failures are silently swallowed and do not produce this
     *         exception
     */
    BookingResponse getBooking(Integer id);
}

