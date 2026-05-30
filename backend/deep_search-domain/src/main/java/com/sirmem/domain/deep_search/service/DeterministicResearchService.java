package com.sirmem.domain.deep_search.service;

import com.sirmem.domain.deep_search.model.contract.ClarifyWithUser;
import com.sirmem.domain.deep_search.model.contract.ConductResearch;
import com.sirmem.domain.deep_search.model.contract.ResearchBriefOutput;
import com.sirmem.domain.deep_search.model.contract.ResearchComplete;
import com.sirmem.domain.deep_search.model.contract.Summary;
import com.sirmem.domain.deep_search.model.value.FinalReport;
import com.sirmem.domain.deep_search.model.value.PlanningContext;
import com.sirmem.domain.deep_search.model.value.RawResearchNote;
import com.sirmem.domain.deep_search.model.value.ResearchBrief;
import com.sirmem.domain.deep_search.model.value.ResearchNote;
import com.sirmem.domain.deep_search.model.value.ResearchQuestion;
import com.sirmem.domain.deep_search.model.value.ResearchTopic;
import com.sirmem.domain.deep_search.workflow.ResearchWorkflowState;
import com.sirmem.domain.deep_search.workflow.ResearcherWorkflowState;
import com.sirmem.types.dto.request.ResearchRequest;
import com.sirmem.types.dto.response.ResearchResponse;
import com.sirmem.types.enumeration.ResearchStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeterministicResearchService implements ResearchService {

    @Override
    public ResearchResponse research(ResearchRequest researchQuestion) {
        ResearchQuestion question = new ResearchQuestion(researchQuestion.question());
        ResearchTopic topic = new ResearchTopic("Understand: " + question.question());
        ConductResearch conductResearch = ConductResearch.builder()
                .researchTopic(topic)
                .build();

        ResearcherWorkflowState researcherState = ResearcherWorkflowState.from(conductResearch)
                .addRawNote(new RawResearchNote("Raw deterministic note for: " + question.question()))
                .compress(Summary.builder()
                        .compressedResearch(new ResearchNote("Structured note for: " + question.question()))
                        .build());

        ResearchWorkflowState state = ResearchWorkflowState.from(question)
                .recordClarification(ClarifyWithUser.verified("Question is specific enough for deterministic AFK workflow."))
                .applyResearchBrief(ResearchBriefOutput.builder()
                        .researchBrief(new ResearchBrief("Research brief for: " + question.question()))
                        .build())
                .plan(
                        new PlanningContext("Use deterministic AFK workflow state without live search or model calls."),
                        List.of(topic)
                )
                .delegate(conductResearch)
                .accumulateResearch(researcherState.toOutput())
                .signalResearchComplete(ResearchComplete.signal());

        state = state.writeFinalReport(buildFinalReport(state));

        return new ResearchResponse(ResearchStatus.COMPLETED, state.finalReportOutput().orElseThrow().content());
    }

    private FinalReport buildFinalReport(ResearchWorkflowState state) {
        return new FinalReport("# Research Report\n\n"
                + "Question: " + state.requestInput().question() + "\n\n"
                + "Brief: " + state.researchBrief().orElseThrow().content() + "\n\n"
                + "Topic: " + state.researchTopics().get(0).content() + "\n\n"
                + "Note: " + state.processedNotes().get(0).content() + "\n\n"
                + "Research completion signal received.\n\n"
                + "This is a deterministic workflow state research report.");
    }
}
