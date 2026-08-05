package com.example.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TransactionApplication {
    /**
     * Bootstraps the Spring Boot application.
     *
     * @param args command-line arguments forwarded to {@link SpringApplication#run}
     */
    public static void main(String[] args) {
        SpringApplication.run(TransactionApplication.class, args);
    }
}
