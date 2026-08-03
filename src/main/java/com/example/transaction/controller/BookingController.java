package com.example.transaction.controller;

import com.example.transaction.dto.BookingResponse;
import com.example.transaction.service.BookingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/booking/{id}")
    public BookingResponse getBooking(@PathVariable Integer id) {
        return bookingService.getBooking(id);
    }
}
