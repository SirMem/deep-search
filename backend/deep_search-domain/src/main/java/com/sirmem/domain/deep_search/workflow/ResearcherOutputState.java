package com.sirmem.domain.deep_search.workflow;

import com.sirmem.domain.deep_search.model.value.RawResearchNote;
import com.sirmem.domain.deep_search.model.value.ResearchNote;

import java.util.List;

public record ResearcherOutputState(
        ResearchNote compressedResearch,
        List<RawResearchNote> rawNotes
) {
    public ResearcherOutputState {
        if (compressedResearch == null) {
            throw new IllegalArgumentException("Compressed research is required.");
        }
        rawNotes = List.copyOf(rawNotes);
    }
}
