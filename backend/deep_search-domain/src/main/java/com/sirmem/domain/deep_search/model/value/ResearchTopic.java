package com.sirmem.domain.deep_search.model.value;

public record ResearchTopic(String content) {
    public ResearchTopic {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Research topic must not be blank.");
        }
        content = content.strip();
    }
}
