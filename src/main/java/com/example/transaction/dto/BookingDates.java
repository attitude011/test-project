package com.example.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDates {

    @JsonProperty("checkin")
    private String checkIn;

    @JsonProperty("checkout")
    private String checkOut;
}

