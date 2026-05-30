package com.sirmem.domain.deep_search.model.value;

public record RawResearchNote(String content) {
    public RawResearchNote {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Raw research note must not be blank.");
        }
        content = content.strip();
    }
}
