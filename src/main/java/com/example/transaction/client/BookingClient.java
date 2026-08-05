package com.example.transaction.client;

import com.example.transaction.dto.BookingResponse;

public interface BookingClient {

    /**
     * Fetches the booking record for the given ID from the external Restful-Booker API.
     *
     * @param id booking identifier; must not be {@code null}
     * @return populated {@link com.example.transaction.dto.BookingResponse}
     * @throws com.example.transaction.exception.BookingApiException on any HTTP or transport failure
     */
    BookingResponse getBooking(Integer id);
}
