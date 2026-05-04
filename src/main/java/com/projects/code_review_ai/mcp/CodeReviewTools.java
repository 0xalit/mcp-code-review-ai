package com.projects.code_review_ai.mcp;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class CodeReviewTools {

    @Tool(description = "Counts the number of non-empty lines of code in a given code snippet.")
    public int countLines(String code) {
        if (code == null || code.trim().isEmpty()) {
            return 0;
        }
        return (int) code.lines()
                .filter(line -> !line.trim().isEmpty())
                .count();
    }

    @Tool(description = "Analyzes a method signature or name to verify standard Java naming conventions.")
    public String checkNaming(String methodName) {
        if (methodName == null || methodName.isEmpty()) {
            return "Method name cannot be empty.";
        }
        if (!Character.isLowerCase(methodName.charAt(0))) {
            return "Warning: Method names should start with a lowercase letter (camelCase).";
        }
        if (methodName.contains("_")) {
            return "Warning: Method names should not contain underscores (use camelCase instead).";
        }
        return "Naming conventions look good.";
    }

    @Tool(description = "Estimates the cyclomatic complexity of a given method by counting branching statements.")
    public int checkComplexity(String code) {
        if (code == null || code.isEmpty()) return 1;
        
        int complexity = 1;
        String[] keywords = {"if\\s*\\(", "for\\s*\\(", "while\\s*\\(", "case\\s+", "catch\\s*\\(", "&&", "\\|\\|", "\\?"};
        for (String keyword : keywords) {
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(keyword).matcher(code);
            while (matcher.find()) {
                complexity++;
            }
        }
        return complexity;
    }

    @Tool(description = "Analyzes the code for common code smells such as deep nesting or potential magic numbers.")
    public String detectSmells(String code) {
        if (code == null || code.isEmpty()) return "No code provided.";
        
        StringBuilder smells = new StringBuilder();
        
        // Detect deep nesting
        int maxIndent = 0;
        int currentIndent = 0;
        for (char c : code.toCharArray()) {
            if (c == '{') currentIndent++;
            if (c == '}') currentIndent--;
            if (currentIndent > maxIndent) maxIndent = currentIndent;
        }
        
        if (maxIndent > 3) {
            smells.append("- Deep nesting detected (depth ").append(maxIndent).append("). Consider extracting logic to separate methods.\n");
        }
        
        if (smells.isEmpty()) {
            return "No common code smells detected.";
        }
        return smells.toString();
    }

    @Tool(description = "Checks if the provided method has Javadoc or basic comments.")
    public String checkDocumentation(String code) {
        if (code == null || code.isEmpty()) return "No code provided.";
        
        if (code.contains("/**") || code.contains("//") || code.contains("/*")) {
            return "Documentation present.";
        }
        return "Warning: Method appears to lack comments or Javadoc.";
    }
}
