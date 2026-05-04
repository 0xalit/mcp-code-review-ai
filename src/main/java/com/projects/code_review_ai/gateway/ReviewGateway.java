package com.projects.code_review_ai.gateway;

import com.projects.code_review_ai.review.ReviewRequest;
import com.projects.code_review_ai.review.ReviewResponse;
import com.projects.code_review_ai.review.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewGateway {

    private static final int MAX_CODE_LENGTH = 10_000;

    private final ReviewService reviewService;

    public ReviewResponse processReview(ReviewRequest request) {
        validate(request);

        log.info("Gateway: request validated, routing to ReviewService ({} chars)",
                request.code().length());

        return reviewService.reviewCode(request);
    }

    // HELPER
    private void validate(ReviewRequest request) {
        // Guard 1: the request object itself must not be null.
        if (request == null) {
            throw new GatewayException("Request body must not be null", HttpStatus.BAD_REQUEST);
        }

        // Guard 2: the code field must not be blank (null or only whitespace)
        if (request.code() == null || request.code().isBlank()) {
            throw new GatewayException("'code' field must not be blank", HttpStatus.BAD_REQUEST);
        }

        // Guard 3: code must not exceed our configured maximum length
        if (request.code().length() > MAX_CODE_LENGTH) {
            throw new GatewayException(
                    "'code' field exceeds the maximum allowed length of " + MAX_CODE_LENGTH + " characters",
                    HttpStatus.BAD_REQUEST);
        }
    }
}
