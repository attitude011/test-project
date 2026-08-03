package com.example.transaction.exception;

public class BookingApiException extends RuntimeException {
    public BookingApiException(String message) {
        super(message);
    }

    public BookingApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

