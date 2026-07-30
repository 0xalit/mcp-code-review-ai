package com.projects.ai_review.service;

import com.projects.ai_review.ai.ReviewAiService;
import com.projects.common.model.ReviewRequest;
import com.projects.common.model.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewAiService reviewAiService;

    public ReviewResponse reviewCode(ReviewRequest request) {
        return reviewAiService.getAiReview(request);
    }
}
