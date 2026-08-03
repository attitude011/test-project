package com.example.transaction.client.impl

import com.example.transaction.dto.TransactionResponseDto
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import spock.lang.Specification

class WebClientTransactionClientSpec extends Specification {

    // Mock the entire WebClient fluent chain
    WebClient.Builder webClientBuilder = Mock()
    WebClient webClient = Mock()
    WebClient.RequestHeadersUriSpec requestUriSpec = Mock()
    WebClient.RequestHeadersSpec requestHeadersSpec = Mock()
    WebClient.ResponseSpec responseSpec = Mock()

    WebClientTransactionClient client

    def setup() {
        // Wire builder → webClient so the constructor call works
        webClientBuilder.build() >> webClient

        client = new WebClientTransactionClient(webClientBuilder)

        // Inject @Value fields via Groovy's direct field access
        client.@baseUrl = "http://serviex.local"
        client.@transactionsPath = "/transactions"
    }

    def "getTransaction should call WebClient and return the mapped response"() {
        given: "the WebClient chain returns a valid DTO"
        def expected = new TransactionResponseDto(500, "TechStore", "EUR", [])
        webClient.get() >> requestUriSpec
        requestUriSpec.uri(*_) >> requestHeadersSpec
        requestHeadersSpec.retrieve() >> responseSpec
        responseSpec.bodyToMono(TransactionResponseDto) >> Mono.just(expected)

        when: "getTransaction is called"
        def result = client.getTransaction("TX-42")

        then: "the response from the chain is returned"
        result == expected
    }

    def "getTransaction should build the URI from baseUrl, transactionsPath and the given id"() {
        given: "stub the remainder of the WebClient chain (retrieve + body)"
        def dto = new TransactionResponseDto(0, "", "", [])
        webClient.get() >> requestUriSpec
        requestHeadersSpec.retrieve() >> responseSpec
        responseSpec.bodyToMono(TransactionResponseDto) >> Mono.just(dto)

        when: "getTransaction is called with a specific id"
        client.getTransaction("TX-99")

        then: "uri is called exactly once with the correct template and the id as varargs"
        1 * requestUriSpec.uri("http://serviex.local/transactions/{id}", _ as Object[]) >> requestHeadersSpec
    }
}

