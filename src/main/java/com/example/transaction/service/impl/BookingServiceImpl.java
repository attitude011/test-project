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
     * Constructs a new {@code BookingServiceImpl} with the required collaborators.
     *
     * @param bookingClient    the client layer responsible for HTTP communication with the
     *                         external Restful-Booker API; must not be {@code null}
     * @param bookingRepository the Spring Data MongoDB repository used to persist booking
     *                         entities for auditing; must not be {@code null}
     */
    public BookingServiceImpl(BookingClient bookingClient, BookingRepository bookingRepository) {
        this.bookingClient = bookingClient;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Fetches the booking record from the external API, attempts to persist it to MongoDB,
     * and returns the response to the caller regardless of database availability.
     *
     * <p>Execution steps:
     * <ol>
     *   <li>Delegates to {@link com.example.transaction.client.BookingClient#getBooking(Integer)}
     *       to retrieve the booking from the external Restful-Booker API.</li>
     *   <li>Builds a {@link com.example.transaction.entity.BookingEntity} with a
     *       {@code savedAt} timestamp of {@link java.time.LocalDateTime#now()} and the
     *       full {@link com.example.transaction.dto.BookingResponse} embedded as
     *       {@code bookingData}.</li>
     *   <li>Saves the entity via the repository. On success, logs an info message.
     *       On failure, logs an error — but the exception is <em>silently swallowed</em>
     *       so that a database outage never degrades the API response.</li>
     *   <li>Returns the original {@link com.example.transaction.dto.BookingResponse}
     *       obtained in step 1.</li>
     * </ol>
     *
     * @param id the unique numeric booking identifier forwarded to the client layer;
     *           must not be {@code null}
     * @return the {@link com.example.transaction.dto.BookingResponse} as returned by the
     *         external API; never {@code null} on a successful upstream call
     * @throws com.example.transaction.exception.BookingApiException if the external API
     *         call in step 1 fails; database failures in step 3 do not produce this exception
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
