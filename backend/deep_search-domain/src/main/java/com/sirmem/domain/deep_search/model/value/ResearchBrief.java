package com.sirmem.domain.deep_search.model.value;

public record ResearchBrief(String content) {
    public ResearchBrief {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Research brief must not be blank.");
        }
        content = content.strip();
    }
}
