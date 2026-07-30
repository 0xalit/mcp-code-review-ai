package com.projects.common.model;

import java.util.List;

public record ReviewResponse(
        String language,
        int score,
        List<Issue> issues,
        List<String> suggestions,
        String summary
) {
}
