package com.projects.gateway.config;

import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    // RestClientBuilderConfigurer applies all Spring Boot auto-configured customizers
    // to our builder including the Micrometer tracing interceptor that forwards
    // the traceparent/b3 headers on outgoing calls so Zipkin can link spans together.
    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder(RestClientBuilderConfigurer configurer) {
        RestClient.Builder builder = RestClient.builder();
        configurer.configure(builder); // applies tracing + other Spring Boot customizers
        return builder;
    }

    @Bean
    public RestClient restClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .baseUrl("http://ai-review-service")
                .build();
    }
}
