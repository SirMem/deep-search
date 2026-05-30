package com.sirmem.domain.deep_search.workflow;

import com.sirmem.domain.deep_search.model.value.RawResearchNote;
import com.sirmem.domain.deep_search.model.value.ResearchBrief;
import com.sirmem.domain.deep_search.model.value.ResearchNote;

import java.util.List;
import java.util.Optional;

public record SupervisorWorkflowState(
        List<String> supervisorMessages,
        Optional<ResearchBrief> researchBrief,
        List<ResearchNote> processedNotes,
        List<RawResearchNote> rawNotes,
        int researchIterationCount
) {
    public SupervisorWorkflowState {
        supervisorMessages = List.copyOf(supervisorMessages);
        researchBrief = researchBrief == null ? Optional.empty() : researchBrief;
        processedNotes = List.copyOf(processedNotes);
        rawNotes = List.copyOf(rawNotes);
        if (researchIterationCount < 0) {
            throw new IllegalArgumentException("Research iteration count must not be negative.");
        }
    }

    public static SupervisorWorkflowState empty() {
        return new SupervisorWorkflowState(List.of(), Optional.empty(), List.of(), List.of(), 0);
    }

    public static SupervisorWorkflowState from(ResearchBrief researchBrief) {
        return empty().withResearchBrief(researchBrief);
    }

    public SupervisorWorkflowState withResearchBrief(ResearchBrief researchBrief) {
        return new SupervisorWorkflowState(
                supervisorMessages,
                Optional.of(researchBrief),
                processedNotes,
                rawNotes,
                researchIterationCount
        );
    }

    public SupervisorWorkflowState nextIteration() {
        return new SupervisorWorkflowState(
                supervisorMessages,
                researchBrief,
                processedNotes,
                rawNotes,
                researchIterationCount + 1
        );
    }

    public SupervisorWorkflowState addResearcherOutput(ResearcherOutputState output) {
        return new SupervisorWorkflowState(
                supervisorMessages,
                researchBrief,
                append(processedNotes, output.compressedResearch()),
                appendAll(rawNotes, output.rawNotes()),
                researchIterationCount
        );
    }

    private static <T> List<T> append(List<T> values, T value) {
        java.util.ArrayList<T> updated = new java.util.ArrayList<>(values);
        updated.add(value);
        return List.copyOf(updated);
    }

    private static <T> List<T> appendAll(List<T> values, List<T> additions) {
        java.util.ArrayList<T> updated = new java.util.ArrayList<>(values);
        updated.addAll(additions);
        return List.copyOf(updated);
    }
}
