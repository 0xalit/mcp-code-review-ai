package com.projects.code_review_ai.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.code_review_ai.review.ReviewRequest;
import com.projects.code_review_ai.review.ReviewResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReviewAiService {

    private final ChatClient chatClient;
    private final ReviewPromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    public ReviewAiService(ChatClient.Builder chatClientBuilder,
            ReviewPromptBuilder promptBuilder,
            ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.promptBuilder = promptBuilder;
        this.objectMapper = objectMapper;
    }

    public ReviewResponse getAiReview(ReviewRequest request) {
        // Step 1: Convert the user's code into a detailed prompt
        String prompt = promptBuilder.buildPrompt(request);

        log.info("Sending prompt to Ollama...");

        // Step 2: Send the prompt to Ollama and get the raw text response back
        String rawJson = chatClient.prompt()
                .user(prompt)
                .functions("countLines", "checkNaming", "checkComplexity", "detectSmells", "checkDocumentation")
                .call()
                .content();

        log.info("Received response from Ollama: {}", rawJson);

        // Step 3: Parse the AI's JSON text into our Java ReviewResponse object
        return parseResponse(rawJson);
    }

    private ReviewResponse parseResponse(String rawJson) {
        try {
            String cleaned = rawJson
                    .replaceAll("(?s)```json\\s*", "") // remove opening ```json
                    .replaceAll("(?s)```\\s*", "") // remove closing ```
                    .trim();

            return objectMapper.readValue(cleaned, ReviewResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", rawJson, e);
            throw new RuntimeException("AI response could not be parsed into a ReviewResponse", e);
        }
    }
}
