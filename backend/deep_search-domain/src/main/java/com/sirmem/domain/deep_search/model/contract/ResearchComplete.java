package com.sirmem.domain.deep_search.model.contract;

public record ResearchComplete() {
    public static ResearchComplete signal() {
        return new ResearchComplete();
    }
}
