package com.example.transaction.apiclient.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Produces a {@link WebClient} pre-configured with the local service base URL
 * ({@code local.api.base-url} in {@code application.yml}).
 */
@Configuration
public class LocalApiClientConfig {

    @Value("${local.api.base-url:http://localhost:8080}")
    private String localApiBaseUrl;

    /**
     * Creates a {@link WebClient} targeting the local API, qualified as {@code "localApiWebClient"}.
     *
     * @param builder Spring Boot auto-configured builder; must not be {@code null}
     * @return {@link WebClient} with base URL set to {@code local.api.base-url}
     */
    @Bean
    @Qualifier("localApiWebClient")
    public WebClient localApiWebClient(WebClient.Builder builder) {
        return builder.baseUrl(localApiBaseUrl).build();
    }
}

