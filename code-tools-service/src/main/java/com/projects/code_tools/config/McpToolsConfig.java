package com.projects.code_tools.config;

import com.projects.code_tools.tools.CodeReviewTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpToolsConfig {

    @Bean
    public ToolCallbackProvider codeToolsCallbackProvider(CodeReviewTools codeReviewTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(codeReviewTools)
                .build();
    }
}
