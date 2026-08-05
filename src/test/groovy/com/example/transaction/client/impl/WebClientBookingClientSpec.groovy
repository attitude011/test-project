package com.example.transaction.client.impl

import com.example.transaction.dto.BookingDates
import com.example.transaction.dto.BookingResponse
import com.example.transaction.exception.BookingApiException
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import spock.lang.Specification

class BookingWebClientImplSpec extends Specification {

    // ─── WebClient fluent-chain mocks ─────────────────────────────────────────
    WebClient.Builder webClientBuilder        = Mock()
    WebClient webClient                       = Mock()
    WebClient.RequestHeadersUriSpec  uriSpec  = Mock()
    WebClient.RequestHeadersSpec     headSpec = Mock()
    WebClient.ResponseSpec           respSpec = Mock()

    BookingWebClientImpl client

    static final String INJECTED_URL     = "https://restful-booker.herokuapp.com/booking/{id}"
    static final String INJECTED_MESSAGE = "ketoKet"

    def setup() {
        webClientBuilder.build() >> webClient
        client = new BookingWebClientImpl(webClientBuilder)
        // Inject @Value fields via Groovy direct private-field access
        client.@apiUrl       = INJECTED_URL
        client.@errorMessage = INJECTED_MESSAGE
    }

    // ─── Success path ──────────────────────────────────────────────────────────

    def "getBooking should return a fully populated BookingResponse on success"() {
        given: "the external API returns a complete booking payload"
        def dates    = new BookingDates("2018-01-01", "2019-01-01")
        def expected = new BookingResponse("Josh", "Allen", 111, true, dates, "super bowls")

        webClient.get()                       >> uriSpec
        uriSpec.uri(*_)                       >> headSpec
        headSpec.retrieve()                   >> respSpec
        respSpec.bodyToMono(BookingResponse)  >> Mono.just(expected)

        when:
        def result = client.getBooking(1)

        then: "every field in the response is correctly mapped"
        result.firstName              == "Josh"
        result.lastName               == "Allen"
        result.totalPrice             == 111
        result.depositPaid            == true
        result.bookingDates           != null
        result.bookingDates.checkIn   == "2018-01-01"
        result.bookingDates.checkOut  == "2019-01-01"
        result.additionalNeeds        == "super bowls"
    }

    // ─── URL injection ─────────────────────────────────────────────────────────

    def "getBooking should call the external API using the injected apiUrl as URI template"() {
        given: "stub the remainder of the WebClient chain"
        def dto = new BookingResponse("Josh", "Allen", 111, true,
                new BookingDates("2018-01-01", "2019-01-01"), "super bowls")

        webClient.get()                      >> uriSpec
        headSpec.retrieve()                  >> respSpec
        respSpec.bodyToMono(BookingResponse) >> Mono.just(dto)

        when:
        client.getBooking(42)

        then: "uri() is called exactly once with the injected URL template and the id as a varargs Object[]"
        1 * uriSpec.uri(INJECTED_URL, _ as Object[]) >> headSpec
    }

    def "apiUrl field holds the value injected from booking.api.url configuration"() {
        expect:
        client.@apiUrl == INJECTED_URL
    }

    // ─── Error-message injection ────────────────────────────────────────────────

    def "errorMessage field holds the value injected from booking.error.message configuration"() {
        expect:
        client.@errorMessage == INJECTED_MESSAGE
    }

    // ─── Error handling ────────────────────────────────────────────────────────

    def "getBooking should throw BookingApiException with the injected error message when Mono emits an error"() {
        given: "the Mono emits an error (simulating any upstream failure)"
        webClient.get()                      >> uriSpec
        uriSpec.uri(*_)                      >> headSpec
        headSpec.retrieve()                  >> respSpec
        respSpec.bodyToMono(BookingResponse) >> Mono.error(new RuntimeException("upstream 500"))

        when:
        client.getBooking(1)

        then: "BookingApiException is thrown carrying the configured error message"
        def ex = thrown(BookingApiException)
        ex.message == INJECTED_MESSAGE
    }

    def "getBooking should wrap the original cause inside BookingApiException on failure"() {
        given: "the Mono emits a specific cause"
        def cause = new RuntimeException("network timeout")
        webClient.get()                      >> uriSpec
        uriSpec.uri(*_)                      >> headSpec
        headSpec.retrieve()                  >> respSpec
        respSpec.bodyToMono(BookingResponse) >> Mono.error(cause)

        when:
        client.getBooking(99)

        then: "the original exception is preserved as the cause"
        def ex = thrown(BookingApiException)
        ex.cause == cause
    }

    def "getBooking should throw BookingApiException even when retrieve() itself throws synchronously"() {
        given: "retrieve() throws before a Mono is ever returned"
        webClient.get()  >> uriSpec
        uriSpec.uri(*_)  >> headSpec
        headSpec.retrieve() >> { throw new RuntimeException("connection refused") }

        when:
        client.getBooking(7)

        then:
        def ex = thrown(BookingApiException)
        ex.message == INJECTED_MESSAGE
    }

    def "getBooking should throw BookingApiException with the injected message when the external API returns HTTP 404"() {
        given: "the external Restful-Booker API responds with 404 Not Found"
        def notFound = WebClientResponseException.create(404, "Not Found", null, null, null)
        webClient.get()                      >> uriSpec
        uriSpec.uri(*_)                      >> headSpec
        headSpec.retrieve()                  >> respSpec
        respSpec.bodyToMono(BookingResponse) >> Mono.error(notFound)

        when:
        client.getBooking(999)

        then: "the 404 is caught and wrapped — the configured error message is used, and the original exception is the cause"
        def ex = thrown(BookingApiException)
        ex.message == INJECTED_MESSAGE
        ex.cause   == notFound
    }

    def "getBooking should throw BookingApiException with the injected message when the external API returns HTTP 500"() {
        given: "the external Restful-Booker API responds with 500 Internal Server Error"
        def serverError = WebClientResponseException.create(500, "Internal Server Error", null, null, null)
        webClient.get()                      >> uriSpec
        uriSpec.uri(*_)                      >> headSpec
        headSpec.retrieve()                  >> respSpec
        respSpec.bodyToMono(BookingResponse) >> Mono.error(serverError)

        when:
        client.getBooking(1)

        then: "the 500 is caught and wrapped — the configured error message is used, and the original exception is the cause"
        def ex = thrown(BookingApiException)
        ex.message == INJECTED_MESSAGE
        ex.cause   == serverError
    }
}
