package com.example.transaction.service.impl;

import com.example.transaction.client.BookingClient;
import com.example.transaction.dto.BookingResponse;
import com.example.transaction.entity.BookingEntity;
import com.example.transaction.repository.BookingRepository;
import com.example.transaction.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingClient bookingClient;
    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingClient bookingClient, BookingRepository bookingRepository) {
        this.bookingClient = bookingClient;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingResponse getBooking(Integer id) {
        BookingResponse bookingResponse = bookingClient.getBooking(id);

        BookingEntity entity = BookingEntity.builder()
                .savedAt(LocalDateTime.now())
                .bookingData(bookingResponse)
                .build();

        try {
            bookingRepository.save(entity);
            log.info("Successfully saved booking to database for ID: {}", id);
        } catch (Exception e) {
            log.error("Failed to save booking to database for ID: {}. Reason: {}", id, e.getMessage(), e);
        }

        return bookingResponse;
    }
}
