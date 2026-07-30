package com.projects.gateway.web;

import com.projects.common.model.ReviewRequest;
import com.projects.common.model.ReviewResponse;
import com.projects.gateway.exception.GatewayException;
import com.projects.gateway.service.ReviewGateway;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewGateway reviewGateway;

    @PostMapping
    public ResponseEntity<ReviewResponse> reviewCode(@RequestBody ReviewRequest request) {
        log.info("Controller: received review request");
        ReviewResponse response = reviewGateway.processReview(request);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(GatewayException.class)
    public ResponseEntity<Map<String, String>> handleGatewayException(GatewayException ex) {
        log.warn("Controller: gateway rejected request — {}", ex.getMessage());
        return ResponseEntity
                .status(ex.getStatus())
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<Map<String, String>> handleCircuitOpen(CallNotPermittedException ex) {
        log.warn("Controller: circuit breaker is OPEN — request rejected immediately");
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "ai-review-service is temporarily unavailable. Please try again shortly."));
    }
}
