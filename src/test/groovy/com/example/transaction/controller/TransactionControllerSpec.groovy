package com.example.transaction.controller

import com.example.transaction.dto.TransactionResponseDto
import com.example.transaction.service.TransactionService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
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
}

