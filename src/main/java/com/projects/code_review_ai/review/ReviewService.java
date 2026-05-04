package com.projects.code_review_ai.review;

import com.projects.code_review_ai.ai.ReviewAiService;
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
