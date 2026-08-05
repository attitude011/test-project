package com.example.transaction.exception;

import com.example.transaction.dto.BookingErrorResponse;
import com.example.transaction.dto.ErrorResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${error.mapping.code:ERR_CONFLICT}")
    private String errorCode;

    @Value("${error.mapping.error:500}")
    private int errorValue;

    /**
     * Handles HTTP 409 (Conflict) responses that are surfaced from the Serviex external
     * service via {@link org.springframework.web.reactive.function.client.WebClientResponseException.Conflict}.
     *
     * <p>Maps the upstream conflict to an HTTP 500 response so that internal integration
     * issues are never directly exposed to API consumers. The response body is built from
     * the {@code error.mapping.code} and {@code error.mapping.error} values in
     * {@code application.yml}.
     *
     * @param exception the {@link org.springframework.web.reactive.function.client.WebClientResponseException.Conflict}
     *                  thrown by the WebClient when Serviex returns HTTP 409; must not be {@code null}
     * @return a {@link ResponseEntity} with HTTP status 500 and an
     *         {@link com.example.transaction.dto.ErrorResponseDto} body containing the
     *         configured {@code code} string and {@code Error} integer
     */
    @ExceptionHandler(WebClientResponseException.Conflict.class)
    public ResponseEntity<ErrorResponseDto> handleConflict(WebClientResponseException.Conflict exception) {
        ErrorResponseDto body = new ErrorResponseDto(errorCode, errorValue);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * Handles {@link com.example.transaction.exception.BookingApiException} thrown by the
     * booking client layer when the external Restful-Booker API call fails.
     *
     * <p>Returns HTTP 500 with a JSON body of the form {@code {"message": "<injected value>"}}
     * where the message is the one carried by the exception (itself sourced from
     * {@code booking.error.message} in {@code application.yml}).
     *
     * @param exception the {@link com.example.transaction.exception.BookingApiException}
     *                  carrying the configured error message; must not be {@code null}
     * @return a {@link ResponseEntity} with HTTP status 500 and a
     *         {@link com.example.transaction.dto.BookingErrorResponse} body
     */
    @ExceptionHandler(BookingApiException.class)
    public ResponseEntity<BookingErrorResponse> handleBookingApiException(BookingApiException exception) {
        BookingErrorResponse body = new BookingErrorResponse(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}


