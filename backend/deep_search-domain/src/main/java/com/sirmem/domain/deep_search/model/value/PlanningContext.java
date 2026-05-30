package com.sirmem.domain.deep_search.model.value;

public record PlanningContext(String strategy) {
    public PlanningContext {
        if (strategy == null || strategy.isBlank()) {
            throw new IllegalArgumentException("Planning context must not be blank.");
        }
        strategy = strategy.strip();
    }
}
