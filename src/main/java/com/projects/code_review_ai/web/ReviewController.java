package com.projects.code_review_ai.web;

import com.projects.code_review_ai.gateway.GatewayException;
import com.projects.code_review_ai.gateway.ReviewGateway;
import com.projects.code_review_ai.review.ReviewRequest;
import com.projects.code_review_ai.review.ReviewResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
