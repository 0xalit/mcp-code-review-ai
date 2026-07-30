// package com.projects.ai_review.ai;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.projects.common.model.ReviewRequest;
// import com.projects.common.model.ReviewResponse;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.ai.chat.client.ChatClient;
// import org.springframework.ai.tool.ToolCallbackProvider;
// import org.springframework.stereotype.Service;

// @Slf4j
// @Service
// public class ReviewAiService {

//     private final ChatClient chatClient;
//     private final ReviewPromptBuilder promptBuilder;
//     private final ObjectMapper objectMapper;
//     private final org.springframework.ai.tool.ToolCallbackProvider toolCallbackProvider;

//     public ReviewAiService(ChatClient.Builder chatClientBuilder,
//                            ReviewPromptBuilder promptBuilder,
//                            ObjectMapper objectMapper,
//                            org.springframework.ai.tool.ToolCallbackProvider toolCallbackProvider) {
//         this.chatClient = chatClientBuilder.build();
//         this.promptBuilder = promptBuilder;
//         this.objectMapper = objectMapper;
//         this.toolCallbackProvider = toolCallbackProvider;
//     }

//     public ReviewResponse getAiReview(ReviewRequest request) {
//         // Step 1: Convert the user's code into a detailed prompt
//         String prompt = promptBuilder.buildPrompt(request);

//         log.info("Sending prompt to Ollama...");

//         // Step 2: Send the prompt to Ollama along with remote MCP tools
//         //
//         // MONOLITH VERSION:
//         //   .functions("countLines", "checkNaming", ...) 
//         //   --> Looked for local Spring Beans inside the SAME JVM container.
//         //
//         // MICROSERVICE VERSION:
//         //   .tools(toolCallbackProvider.getToolCallbacks())
//         //   --> Dynamically pulls remote tools exposed by code-tools-service (Port 8082) over MCP!
//         String rawJson = chatClient.prompt()
//                 .user(prompt)
//                 .tools(toolCallbackProvider.getToolCallbacks())
//                 .call()
//                 .content();

//         log.info("Received response from Ollama: {}", rawJson);

//         // Step 3: Parse the AI's JSON text into our Java ReviewResponse object
//         return parseResponse(rawJson);
//     }

//     private ReviewResponse parseResponse(String rawJson) {
//         try {
//             String cleaned = rawJson
//                     .replaceAll("(?s)```json\\s*", "") // remove opening ```json
//                     .replaceAll("(?s)```\\s*", "")     // remove closing ```
//                     .trim();

//             return objectMapper.readValue(cleaned, ReviewResponse.class);
//         } catch (Exception e) {
//             log.error("Failed to parse AI response: {}", rawJson, e);
//             throw new RuntimeException("AI response could not be parsed into a ReviewResponse", e);
//         }
//     }
// }

package com.projects.ai_review.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.common.model.ReviewRequest;
import com.projects.common.model.ReviewResponse;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReviewAiService {

    // Spring AI client used to send prompts and tools to the LLM (Ollama) and receive model responses
    private final ChatClient chatClient;

    // Component responsible for building the review prompt and output JSON structure instructions
    private final ReviewPromptBuilder promptBuilder;

    // Jackson ObjectMapper used to parse raw JSON responses from the AI into Java objects
    private final ObjectMapper objectMapper;

    // Spring AI MCP provider that collects remote MCP tools exposed by code-tools-service
    private final ToolCallbackProvider toolCallbackProvider;

    public ReviewAiService(ChatClient.Builder chatClientBuilder,
            ReviewPromptBuilder promptBuilder,
            ObjectMapper objectMapper,
            ToolCallbackProvider toolCallbackProvider) {
        this.chatClient = chatClientBuilder.build();
        this.promptBuilder = promptBuilder;
        this.objectMapper = objectMapper;
        this.toolCallbackProvider = toolCallbackProvider;
    }

    // SEMAPHORE bulkhead: limits how many threads can call Ollama simultaneously.
    @Bulkhead(name = "ollama", type = Bulkhead.Type.SEMAPHORE)
    public ReviewResponse getAiReview(ReviewRequest request) {
        // Step 1: Convert the user's code into a detailed prompt
        String prompt = promptBuilder.buildPrompt(request);

        log.info("Sending prompt to Ollama...");

        // Step 2: Send the prompt to Ollama along with remote MCP tools.
        // tools are provided over MCP via toolCallbackProvider.getToolCallbacks()
        String rawJson = chatClient.prompt()
                .user(prompt)
                .tools(toolCallbackProvider.getToolCallbacks())
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