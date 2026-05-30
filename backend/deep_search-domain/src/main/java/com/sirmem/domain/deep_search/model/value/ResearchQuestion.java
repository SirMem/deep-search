package com.sirmem.domain.deep_search.model.value;

public record ResearchQuestion(String question) {
    public ResearchQuestion {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Research question must not be blank.");
        }
        question = question.strip();
    }
}
