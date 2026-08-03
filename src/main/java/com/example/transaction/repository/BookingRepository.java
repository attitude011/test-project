package com.example.transaction.repository;

import com.example.transaction.entity.BookingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingRepository extends MongoRepository<BookingEntity, String> {
}

