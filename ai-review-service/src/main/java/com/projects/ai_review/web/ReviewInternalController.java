package com.projects.ai_review.web;

import com.projects.ai_review.service.ReviewService;
import com.projects.common.model.ReviewRequest;
import com.projects.common.model.ReviewResponse;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewInternalController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> processReview(@RequestBody ReviewRequest request) {
        log.info("AI Review Service: received review request from gateway");
        ReviewResponse response = reviewService.reviewCode(request);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(BulkheadFullException.class)
    public ResponseEntity<Map<String, String>> handleBulkheadFull(BulkheadFullException ex) {
        log.warn("AI Review Service: Ollama bulkhead is full — rejecting concurrent request");
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Ollama is busy processing another request. Please try again shortly."));
    }
}
