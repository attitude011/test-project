package com.example.transaction.service.impl

import com.example.transaction.client.BookingClient
import com.example.transaction.dto.BookingDates
import com.example.transaction.dto.BookingResponse
import com.example.transaction.entity.BookingEntity
import com.example.transaction.exception.BookingApiException
import com.example.transaction.repository.BookingRepository
import spock.lang.Specification

class BookingServiceImplSpec extends Specification {

    BookingClient      bookingClient      = Mock()
    BookingRepository  bookingRepository  = Mock()
    BookingServiceImpl service

    def setup() {
        service = new BookingServiceImpl(bookingClient, bookingRepository)
    }

    // ─── Delegation & return value ─────────────────────────────────────────────

    def "getBooking should delegate to bookingClient and return the BookingResponse"() {
        given: "a fully populated BookingResponse"
        def dates    = new BookingDates("2018-01-01", "2019-01-01")
        def expected = new BookingResponse("Josh", "Allen", 111, true, dates, "super bowls")

        when:
        def result = service.getBooking(1)

        then: "bookingClient is called exactly once with the correct id"
        1 * bookingClient.getBooking(1) >> expected
        and: "the original BookingResponse is returned to the caller"
        result == expected
    }

    def "getBooking should pass the id through to bookingClient unchanged"() {
        given:
        def dto = new BookingResponse("A", "B", 0, false, new BookingDates("", ""), "")

        when:
        service.getBooking(99)

        then:
        1 * bookingClient.getBooking(99) >> dto
    }

    // ─── MongoDB save logic ────────────────────────────────────────────────────

    def "getBooking should save a BookingEntity embedding the full BookingResponse"() {
        given: "the client returns a booking"
        def dates    = new BookingDates("2018-01-01", "2019-01-01")
        def response = new BookingResponse("Josh", "Allen", 111, true, dates, "super bowls")
        bookingClient.getBooking(7) >> response

        when:
        service.getBooking(7)

        then: "repository.save is called once with an entity containing the full BookingResponse"
        1 * bookingRepository.save({ BookingEntity e ->
            e.bookingData == response &&
            e.savedAt     != null
        })
    }

    def "getBooking should still return BookingResponse even when repository.save throws"() {
        given: "the client returns a booking but the database is down"
        def dates    = new BookingDates("2018-01-01", "2019-01-01")
        def response = new BookingResponse("Josh", "Allen", 111, true, dates, "super bowls")
        bookingClient.getBooking(3) >> response
        bookingRepository.save(_) >> { throw new RuntimeException("MongoDB unreachable") }

        when: "getBooking is called despite the DB being down"
        def result = service.getBooking(3)

        then: "no exception propagates — the API flow continues"
        noExceptionThrown()
        and: "the original BookingResponse is still returned"
        result == response
    }

    def "getBooking should not propagate any exception from repository.save"() {
        given:
        def response = new BookingResponse("X", "Y", 0, false, new BookingDates("", ""), "")
        bookingClient.getBooking(10) >> response
        bookingRepository.save(_) >> { throw new RuntimeException("connection timeout") }

        when:
        service.getBooking(10)

        then:
        noExceptionThrown()
    }

    // ─── Error propagation from client ────────────────────────────────────────

    def "getBooking should propagate BookingApiException thrown by bookingClient"() {
        given: "the HTTP client fails"
        bookingClient.getBooking(1) >> { throw new BookingApiException("ketoKet") }

        when:
        service.getBooking(1)

        then: "exception bubbles up before any DB save is attempted"
        def ex = thrown(BookingApiException)
        ex.message == "ketoKet"
        and: "repository is never touched"
        0 * bookingRepository.save(_)
    }

    def "getBooking should not swallow RuntimeException thrown by bookingClient"() {
        given:
        bookingClient.getBooking(5) >> { throw new RuntimeException("unexpected") }

        when:
        service.getBooking(5)

        then:
        thrown(RuntimeException)
        and:
        0 * bookingRepository.save(_)
    }
}
