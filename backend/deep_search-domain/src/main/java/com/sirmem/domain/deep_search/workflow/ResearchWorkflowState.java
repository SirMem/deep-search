package com.sirmem.domain.deep_search.workflow;

import com.sirmem.domain.deep_search.model.contract.ClarifyWithUser;
import com.sirmem.domain.deep_search.model.contract.ConductResearch;
import com.sirmem.domain.deep_search.model.contract.ResearchBriefOutput;
import com.sirmem.domain.deep_search.model.contract.ResearchComplete;
import com.sirmem.domain.deep_search.model.enumeration.ResearchWorkflowStage;
import com.sirmem.domain.deep_search.model.value.FinalReport;
import com.sirmem.domain.deep_search.model.value.PlanningContext;
import com.sirmem.domain.deep_search.model.value.RawResearchNote;
import com.sirmem.domain.deep_search.model.value.ResearchBrief;
import com.sirmem.domain.deep_search.model.value.ResearchNote;
import com.sirmem.domain.deep_search.model.value.ResearchQuestion;
import com.sirmem.domain.deep_search.model.value.ResearchTopic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ResearchWorkflowState(
        ResearchQuestion requestInput,
        Optional<ClarifyWithUser> clarificationDecision,
        Optional<ResearchBrief> researchBrief,
        Optional<PlanningContext> planningContext,
        List<ResearchTopic> researchTopics,
        List<ResearchNote> processedNotes,
        List<RawResearchNote> rawNotes,
        Optional<ResearchComplete> researchCompleteSignal,
        Optional<FinalReport> finalReportOutput,
        ResearchWorkflowStage stage
) {
    public ResearchWorkflowState {
        if (requestInput == null) {
            throw new IllegalArgumentException("Request input is required.");
        }
        clarificationDecision = clarificationDecision == null ? Optional.empty() : clarificationDecision;
        researchBrief = researchBrief == null ? Optional.empty() : researchBrief;
        planningContext = planningContext == null ? Optional.empty() : planningContext;
        researchTopics = List.copyOf(researchTopics);
        processedNotes = List.copyOf(processedNotes);
        rawNotes = List.copyOf(rawNotes);
        researchCompleteSignal = researchCompleteSignal == null ? Optional.empty() : researchCompleteSignal;
        finalReportOutput = finalReportOutput == null ? Optional.empty() : finalReportOutput;
        if (stage == null) {
            throw new IllegalArgumentException("Workflow stage is required.");
        }
    }

    public static ResearchWorkflowState from(ResearchQuestion requestInput) {
        return new ResearchWorkflowState(
                requestInput,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                List.of(),
                List.of(),
                List.of(),
                Optional.empty(),
                Optional.empty(),
                ResearchWorkflowStage.QUESTION_RECEIVED
        );
    }

    public ResearchWorkflowState recordClarification(ClarifyWithUser clarification) {
        return new ResearchWorkflowState(
                requestInput,
                Optional.of(clarification),
                researchBrief,
                planningContext,
                researchTopics,
                processedNotes,
                rawNotes,
                researchCompleteSignal,
                finalReportOutput,
                clarification.isNeedClarification()
                        ? ResearchWorkflowStage.CLARIFICATION_NEEDED
                        : ResearchWorkflowStage.CLARIFICATION_VERIFIED
        );
    }

    public ResearchWorkflowState applyResearchBrief(ResearchBriefOutput output) {
        return new ResearchWorkflowState(
                requestInput,
                clarificationDecision,
                Optional.of(output.getResearchBrief()),
                planningContext,
                researchTopics,
                processedNotes,
                rawNotes,
                researchCompleteSignal,
                finalReportOutput,
                ResearchWorkflowStage.BRIEF_GENERATED
        );
    }

    public ResearchWorkflowState plan(PlanningContext planningContext, List<ResearchTopic> topics) {
        return new ResearchWorkflowState(
                requestInput,
                clarificationDecision,
                researchBrief,
                Optional.of(planningContext),
                List.copyOf(topics),
                processedNotes,
                rawNotes,
                researchCompleteSignal,
                finalReportOutput,
                ResearchWorkflowStage.RESEARCH_PLANNED
        );
    }

    public ResearchWorkflowState delegate(ConductResearch command) {
        return new ResearchWorkflowState(
                requestInput,
                clarificationDecision,
                researchBrief,
                planningContext,
                includeTopic(researchTopics, command.getResearchTopic()),
                processedNotes,
                rawNotes,
                researchCompleteSignal,
                finalReportOutput,
                ResearchWorkflowStage.RESEARCH_IN_PROGRESS
        );
    }

    public ResearchWorkflowState accumulateResearch(ResearcherOutputState researcherOutput) {
        return new ResearchWorkflowState(
                requestInput,
                clarificationDecision,
                researchBrief,
                planningContext,
                researchTopics,
                append(processedNotes, researcherOutput.compressedResearch()),
                appendAll(rawNotes, researcherOutput.rawNotes()),
                researchCompleteSignal,
                finalReportOutput,
                ResearchWorkflowStage.RESEARCH_ACCUMULATED
        );
    }

    public ResearchWorkflowState signalResearchComplete(ResearchComplete signal) {
        return new ResearchWorkflowState(
                requestInput,
                clarificationDecision,
                researchBrief,
                planningContext,
                researchTopics,
                processedNotes,
                rawNotes,
                Optional.of(signal),
                finalReportOutput,
                ResearchWorkflowStage.RESEARCH_COMPLETE_SIGNALED
        );
    }

    public ResearchWorkflowState writeFinalReport(FinalReport finalReport) {
        return new ResearchWorkflowState(
                requestInput,
                clarificationDecision,
                researchBrief,
                planningContext,
                researchTopics,
                processedNotes,
                rawNotes,
                researchCompleteSignal,
                Optional.of(finalReport),
                ResearchWorkflowStage.FINAL_REPORT_GENERATED
        );
    }

    private static List<ResearchTopic> includeTopic(List<ResearchTopic> topics, ResearchTopic topic) {
        if (topics.contains(topic)) {
            return topics;
        }
        return append(topics, topic);
    }

    private static <T> List<T> append(List<T> values, T value) {
        ArrayList<T> updated = new ArrayList<>(values);
        updated.add(value);
        return List.copyOf(updated);
    }

    private static <T> List<T> appendAll(List<T> values, List<T> additions) {
        ArrayList<T> updated = new ArrayList<>(values);
        updated.addAll(additions);
        return List.copyOf(updated);
    }
}
