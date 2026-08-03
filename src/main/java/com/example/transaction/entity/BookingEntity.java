package com.example.transaction.entity;

import com.example.transaction.dto.BookingResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "booking")
public class BookingEntity {

    @Id
    private String id;

    private LocalDateTime savedAt;

    private BookingResponse bookingData;
}

