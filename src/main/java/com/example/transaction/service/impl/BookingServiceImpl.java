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

    /**
     * @param bookingClient     HTTP client for the external Restful-Booker API; must not be {@code null}
     * @param bookingRepository MongoDB repository for persistence; must not be {@code null}
     */
    public BookingServiceImpl(BookingClient bookingClient, BookingRepository bookingRepository) {
        this.bookingClient = bookingClient;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Fetches the booking, saves it to MongoDB (failures are swallowed), and returns the response.
     *
     * @param id booking identifier; must not be {@code null}
     * @return populated {@link BookingResponse}
     * @throws com.example.transaction.exception.BookingApiException on external API failure
     */
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
