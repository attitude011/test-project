package com.example.transaction.service.impl

import com.example.transaction.client.TransactionClient
import com.example.transaction.dto.TransactionResponseDto
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
}

