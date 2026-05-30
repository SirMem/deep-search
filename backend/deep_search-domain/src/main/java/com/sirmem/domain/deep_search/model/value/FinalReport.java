package com.sirmem.domain.deep_search.model.value;

public record FinalReport(String content) {
    public FinalReport {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Final report must not be blank.");
        }
    }
}
