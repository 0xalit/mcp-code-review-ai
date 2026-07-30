package com.projects.gateway.service;

import com.projects.common.model.Issue;
import com.projects.common.model.ReviewRequest;
import com.projects.common.model.ReviewResponse;
import com.projects.gateway.exception.GatewayException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewGateway {

    private static final int MAX_CODE_LENGTH = 10_000;

    private final RestClient restClient;

    @Retry(name = "ai-review")
    @CircuitBreaker(name = "ai-review", fallbackMethod = "fallbackReview")
    public ReviewResponse processReview(ReviewRequest request) {
        validate(request);

        log.info("Gateway: request validated, forwarding to ai-review-service ({} chars)",
                request.code().length());

        return restClient.post() // 1. POST
                .uri("/api/v1/review") // 2. Send to this path on ai-review-service
                .body(request) // 3. Set request body
                .retrieve() // 4. Execute HTTP call
                .body(ReviewResponse.class); // 5. Parse response
    }

    private ReviewResponse fallbackReview(ReviewRequest request, Throwable cause) {
        log.warn("Gateway: circuit breaker fallback triggered — {}", cause.getMessage());
        return new ReviewResponse(
                "unknown",
                0,
                List.of(new Issue("HIGH", "ai-review-service is currently unavailable")),
                List.of(),
                "The review service could not be reached. Please try again in a few moments."
        );
    }

    private void validate(ReviewRequest request) {
        if (request == null) {
            throw new GatewayException("Request body must not be null", HttpStatus.BAD_REQUEST);
        }

        if (request.code() == null || request.code().isBlank()) {
            throw new GatewayException("'code' field must not be blank", HttpStatus.BAD_REQUEST);
        }

        if (request.code().length() > MAX_CODE_LENGTH) {
            throw new GatewayException(
                    "'code' field exceeds the maximum allowed length of " + MAX_CODE_LENGTH + " characters",
                    HttpStatus.BAD_REQUEST);
        }
    }
}
