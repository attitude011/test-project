package com.example.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TransactionApplication {
    /**
     * Application entry point. Bootstraps the Spring Boot context and starts the embedded server.
     *
     * @param args command-line arguments passed to the JVM at startup; forwarded directly to
     *             {@link SpringApplication#run(Class, String...)} for Spring-standard processing
     *             (e.g. {@code --server.port=9090})
     */
    public static void main(String[] args) {
        SpringApplication.run(TransactionApplication.class, args);
    }
}
