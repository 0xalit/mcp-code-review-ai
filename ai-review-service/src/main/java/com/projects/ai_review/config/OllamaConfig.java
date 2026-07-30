package com.projects.ai_review.config;

import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
public class OllamaConfig {

    @Value("${spring.ai.ollama.base-url}")
    private String ollamaBaseUrl;

    @Bean
    public OllamaApi ollamaApi() {
        // Configure the HTTP client with explicit timeouts.
        // Without this, the ChatClient call to Ollama has NO timeout — a slow
        // or hung Ollama process would block the thread indefinitely.
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // How long to wait when opening the TCP connection to Ollama
        factory.setConnectTimeout(Duration.ofSeconds(5));

        factory.setReadTimeout(Duration.ofSeconds(120));

        RestClient.Builder restClientBuilder = RestClient.builder()
                .requestFactory(factory);

        return new OllamaApi(ollamaBaseUrl, restClientBuilder, WebClient.builder());
    }
}
