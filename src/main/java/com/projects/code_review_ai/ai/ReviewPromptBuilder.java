package com.projects.code_review_ai.ai;

import com.projects.code_review_ai.review.ReviewRequest;
import org.springframework.stereotype.Component;

@Component
public class ReviewPromptBuilder {

  public String buildPrompt(ReviewRequest request) {
    return """
        You are an expert code reviewer. Your job is to analyze the code below and identify issues.

        Analyze this code:
        ```
        %s
        ```

        Respond ONLY with a valid JSON object. Do not add any explanation before or after the JSON.
        Use exactly this structure:
        {
          "language": "<detected programming language>",
          "score": <a number from 0 to 100 rating overall code quality>,
          "issues": [
            { "severity": "<HIGH, MEDIUM, or LOW>", "message": "<description of the issue>" }
          ],
          "suggestions": ["<one actionable suggestion per item>"],
          "summary": "<one paragraph summarizing the review>"
        }
        """.formatted(request.code());
  }
}
