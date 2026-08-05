package com.example.transaction.exception;

public class BookingApiException extends RuntimeException {

    /**
     * @param message error message exposed to API consumers via the global exception handler
     */
    public BookingApiException(String message) {
        super(message);
    }

    /**
     * @param message error message exposed to API consumers via the global exception handler
     * @param cause   originating exception; may be {@code null}
     */
    public BookingApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
