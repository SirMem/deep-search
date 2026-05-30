package com.sirmem.domain.deep_search.model.value;

public record ResearchNote(String content) {
    public ResearchNote {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Research note must not be blank.");
        }
        content = content.strip();
    }
}
