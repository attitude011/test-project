package com.example.transaction.client;

import com.example.transaction.dto.BookingResponse;

public interface BookingClient {
    BookingResponse getBooking(Integer id);
}

