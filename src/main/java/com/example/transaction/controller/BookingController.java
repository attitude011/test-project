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
     * @param bookingService service delegate; must not be {@code null}
     */
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * {@code GET /booking/{id}} — returns the booking record for the given ID.
     *
     * @param id numeric booking identifier
     * @return populated {@link com.example.transaction.dto.BookingResponse}
     * @throws com.example.transaction.exception.BookingApiException on upstream failure; mapped to HTTP 500
     */
    @GetMapping("/booking/{id}")
    public BookingResponse getBooking(@PathVariable Integer id) {
        return bookingService.getBooking(id);
    }
}
