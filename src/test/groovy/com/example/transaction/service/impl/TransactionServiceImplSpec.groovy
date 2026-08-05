package com.example.transaction.service.impl

import com.example.transaction.client.TransactionClient
import com.example.transaction.dto.TransactionResponseDto
import org.springframework.web.reactive.function.client.WebClientResponseException
import spock.lang.Specification

class TransactionServiceImplSpec extends Specification {

    TransactionClient transactionClient = Mock()
    TransactionServiceImpl service

    def setup() {
        service = new TransactionServiceImpl(transactionClient)
    }

    def "getTransaction should delegate to transactionClient and return its result"() {
        given: "a populated TransactionResponseDto to return"
        def expected = new TransactionResponseDto(300, "Shop", "GBP", [])

        when: "service.getTransaction is called"
        def result = service.getTransaction("ID-99")

        then: "the client is called exactly once with the correct id and returns the expected dto"
        1 * transactionClient.getTransaction("ID-99") >> expected
        and: "the result equals what the client returned"
        result == expected
    }

    def "getTransaction should propagate null when client returns null"() {
        given: "the client returns null"
        transactionClient.getTransaction("MISSING") >> null

        when: "service.getTransaction is called"
        def result = service.getTransaction("MISSING")

        then: "null is propagated to the caller"
        result == null
    }

    // ─── Error handling ────────────────────────────────────────────────────────

    def "getTransaction should propagate WebClientResponseException.Conflict thrown by the client without wrapping"() {
        given: "the Serviex client throws a 409 Conflict"
        def conflict = WebClientResponseException.create(409, "Conflict", null, null, null)
        transactionClient.getTransaction("TX-409") >> { throw conflict }

        when:
        service.getTransaction("TX-409")

        then: "the Conflict exception propagates unchanged so GlobalExceptionHandler can remap it to HTTP 500"
        def ex = thrown(WebClientResponseException.Conflict)
        ex.rawStatusCode == 409
    }

    def "getTransaction should propagate any RuntimeException thrown by the client without wrapping or swallowing"() {
        given: "the client throws a generic connection error"
        transactionClient.getTransaction("TX-ERR") >> { throw new RuntimeException("network timeout") }

        when:
        service.getTransaction("TX-ERR")

        then: "the exception propagates unchanged — the service layer introduces no catch block"
        def ex = thrown(RuntimeException)
        ex.message == "network timeout"
    }

    def "getTransaction should propagate WebClientResponseException for any non-2xx response other than 409"() {
        given: "the client throws a 503 Service Unavailable"
        def serviceUnavailable = WebClientResponseException.create(503, "Service Unavailable", null, null, null)
        transactionClient.getTransaction("TX-503") >> { throw serviceUnavailable }

        when:
        service.getTransaction("TX-503")

        then: "the exception propagates; the service does not swallow non-conflict HTTP errors"
        def ex = thrown(WebClientResponseException)
        ex.rawStatusCode == 503
    }
}

