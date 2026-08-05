package com.example.transaction.controller

import com.example.transaction.dto.TransactionResponseDto
import com.example.transaction.exception.GlobalExceptionHandler
import com.example.transaction.service.TransactionService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.util.NestedServletException
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class TransactionControllerSpec extends Specification {

    TransactionService transactionService = Mock()
    MockMvc mockMvc

    def setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TransactionController(transactionService))
                .build()
    }

    def "getTransaction should return HTTP 200 and correct transaction body for a valid id"() {
        given: "the service returns a populated TransactionResponseDto"
        def dto = new TransactionResponseDto(150, "MainStore", "USD", [])
        transactionService.getTransaction("TXN-01") >> dto

        when: "the client calls GET /getTrx/TXN-01"
        def result = mockMvc.perform(
                get("/getTrx/TXN-01").accept(MediaType.APPLICATION_JSON))

        then: "the response is 200 OK with the correct JSON fields"
        result.andExpect(status().isOk())
              .andExpect(jsonPath('$.amount').value(150))
              .andExpect(jsonPath('$.store').value("MainStore"))
              .andExpect(jsonPath('$.currency').value("USD"))
    }

    def "getTransaction should delegate exactly once to transactionService with the path variable id"() {
        given: "the service returns any dto"
        def dto = new TransactionResponseDto(0, "S", "EUR", [])
        transactionService.getTransaction("ABC-99") >> dto

        when: "the controller handles the request"
        mockMvc.perform(get("/getTrx/ABC-99"))

        then: "service.getTransaction is called exactly once with the correct id"
        1 * transactionService.getTransaction("ABC-99")
    }

    // ─── Error handling ────────────────────────────────────────────────────────

    def "getTransaction should return HTTP 500 with configured error body when service surfaces a WebClientResponseException.Conflict"() {
        given: "a GlobalExceptionHandler wired with known error values"
        def handler = new GlobalExceptionHandler()
        handler.@errorCode  = "SERVIEX_CONFLICT"
        handler.@errorValue = 500
        def mvc = MockMvcBuilders
                .standaloneSetup(new TransactionController(transactionService))
                .setControllerAdvice(handler)
                .build()
        and: "the service propagates a 409 Conflict from Serviex"
        transactionService.getTransaction("TXN-409") >> {
            throw WebClientResponseException.create(409, "Conflict", null, null, null)
        }

        when:
        def result = mvc.perform(get("/getTrx/TXN-409").accept(MediaType.APPLICATION_JSON))

        then: "GlobalExceptionHandler translates it to HTTP 500 with the configured code and Error fields"
        result.andExpect(status().isInternalServerError())
              .andExpect(jsonPath('$.code').value("SERVIEX_CONFLICT"))
              .andExpect(jsonPath('$.Error').value(500))
    }

    def "getTransaction should propagate an unhandled RuntimeException as NestedServletException when no matching handler exists"() {
        given: "the service throws a generic exception not covered by GlobalExceptionHandler"
        transactionService.getTransaction("TXN-BOOM") >> { throw new RuntimeException("unexpected internal error") }

        when: "MockMvc dispatches the request — no ControllerAdvice is installed in this setup"
        mockMvc.perform(get("/getTrx/TXN-BOOM"))

        then: "MockMvc wraps the unhandled exception in a NestedServletException and re-throws it"
        def ex = thrown(NestedServletException)
        ex.cause instanceof RuntimeException
        ex.cause.message == "unexpected internal error"
    }
}

