package com.example.transaction.apiclient.exception;

/**
 * Thrown by API client classes when a call to a local service endpoint fails.
 */
public class ApiClientException extends RuntimeException {

    /**
     * @param message description of why the call failed; must not be {@code null}
     */
    public ApiClientException(String message) {
        super(message);
    }

    /**
     * @param message description of why the call failed; must not be {@code null}
     * @param cause   originating exception; may be {@code null}
     */
    public ApiClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
