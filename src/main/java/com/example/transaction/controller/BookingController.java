package com.example.transaction.controller;

import com.example.transaction.dto.BookingResponse;
import com.example.transaction.service.BookingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingController {

    private final BookingService bookingService;

    /**
     * Constructs a new {@code BookingController} with the provided booking service.
     *
     * @param bookingService the service layer responsible for orchestrating the booking
     *                       lookup and persistence logic; must not be {@code null}
     */
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Handles {@code GET /booking/{id}} requests by delegating to the booking service layer.
     *
     * <p>Returns the full booking record for the given numeric identifier as fetched from
     * the external Restful-Booker API. If the downstream call fails, a
     * {@link com.example.transaction.exception.BookingApiException} is propagated and
     * translated to HTTP 500 by the global exception handler.
     *
     * @param id the unique numeric booking identifier supplied as a URI path variable;
     *           must not be {@code null}
     * @return the {@link com.example.transaction.dto.BookingResponse} containing firstname,
     *         lastname, totalprice, depositpaid, bookingdates, and additionalneeds
     * @throws com.example.transaction.exception.BookingApiException if the external API
     *         call fails or the response cannot be deserialised
     */
    @GetMapping("/booking/{id}")
    public BookingResponse getBooking(@PathVariable Integer id) {
        return bookingService.getBooking(id);
    }
}
