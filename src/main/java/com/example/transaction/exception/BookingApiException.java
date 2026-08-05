package com.example.transaction.exception;

public class BookingApiException extends RuntimeException {

    /**
     * Constructs a new {@code BookingApiException} with the specified detail message and no cause.
     * Use this constructor when the error originates at the application level without an
     * underlying throwable.
     *
     * @param message the human-readable error message injected from {@code booking.error.message}
     *                in {@code application.yml}; exposed to API consumers via the global
     *                exception handler
     */
    public BookingApiException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code BookingApiException} wrapping an underlying cause. Use this
     * constructor when the exception originates from a caught {@link Exception} (e.g., a
     * WebClient failure) and the original stack trace should be preserved for diagnostics.
     *
     * @param message the human-readable error message injected from {@code booking.error.message}
     *                in {@code application.yml}; exposed to API consumers via the global
     *                exception handler
     * @param cause   the originating exception that triggered this domain exception;
     *                may be {@code null} if no root cause is available
     */
    public BookingApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

