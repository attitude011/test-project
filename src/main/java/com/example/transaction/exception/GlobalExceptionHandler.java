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

    @ExceptionHandler(WebClientResponseException.Conflict.class)
    public ResponseEntity<ErrorResponseDto> handleConflict(WebClientResponseException.Conflict exception) {
        ErrorResponseDto body = new ErrorResponseDto(errorCode, errorValue);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(BookingApiException.class)
    public ResponseEntity<BookingErrorResponse> handleBookingApiException(BookingApiException exception) {
        BookingErrorResponse body = new BookingErrorResponse(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}


