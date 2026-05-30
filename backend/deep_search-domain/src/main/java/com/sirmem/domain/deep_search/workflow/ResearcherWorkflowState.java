package com.sirmem.domain.deep_search.workflow;

import com.sirmem.domain.deep_search.model.contract.ConductResearch;
import com.sirmem.domain.deep_search.model.contract.Summary;
import com.sirmem.domain.deep_search.model.value.RawResearchNote;
import com.sirmem.domain.deep_search.model.value.ResearchNote;
import com.sirmem.domain.deep_search.model.value.ResearchTopic;

import java.util.List;
import java.util.Optional;

public record ResearcherWorkflowState(
        List<String> researcherMessages,
        ResearchTopic researchTopic,
        List<RawResearchNote> rawNotes,
        Optional<ResearchNote> compressedResearch,
        int toolCallIterationCount
) {
    public ResearcherWorkflowState {
        researcherMessages = List.copyOf(researcherMessages);
        if (researchTopic == null) {
            throw new IllegalArgumentException("Research topic is required.");
        }
        rawNotes = List.copyOf(rawNotes);
        compressedResearch = compressedResearch == null ? Optional.empty() : compressedResearch;
        if (toolCallIterationCount < 0) {
            throw new IllegalArgumentException("Tool-call iteration count must not be negative.");
        }
    }

    public static ResearcherWorkflowState from(ConductResearch command) {
        return new ResearcherWorkflowState(List.of(), command.getResearchTopic(), List.of(), Optional.empty(), 0);
    }

    public ResearcherWorkflowState addRawNote(RawResearchNote rawNote) {
        java.util.ArrayList<RawResearchNote> updatedRawNotes = new java.util.ArrayList<>(rawNotes);
        updatedRawNotes.add(rawNote);
        return new ResearcherWorkflowState(
                researcherMessages,
                researchTopic,
                updatedRawNotes,
                compressedResearch,
                toolCallIterationCount
        );
    }

    public ResearcherWorkflowState compress(Summary summary) {
        return new ResearcherWorkflowState(
                researcherMessages,
                researchTopic,
                rawNotes,
                Optional.of(summary.getCompressedResearch()),
                toolCallIterationCount
        );
    }

    public ResearcherWorkflowState nextToolCallIteration() {
        return new ResearcherWorkflowState(
                researcherMessages,
                researchTopic,
                rawNotes,
                compressedResearch,
                toolCallIterationCount + 1
        );
    }

    public ResearcherOutputState toOutput() {
        return new ResearcherOutputState(
                compressedResearch.orElseThrow(() -> new IllegalStateException("Compressed research is required.")),
                rawNotes
        );
    }
}
