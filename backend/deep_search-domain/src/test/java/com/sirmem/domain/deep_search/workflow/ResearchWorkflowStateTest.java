package com.sirmem.domain.deep_search.workflow;

import com.sirmem.domain.deep_search.model.contract.ClarifyWithUser;
import com.sirmem.domain.deep_search.model.contract.ConductResearch;
import com.sirmem.domain.deep_search.model.contract.ResearchBriefOutput;
import com.sirmem.domain.deep_search.model.contract.ResearchComplete;
import com.sirmem.domain.deep_search.model.contract.Summary;
import com.sirmem.domain.deep_search.model.enumeration.ResearchWorkflowStage;
import com.sirmem.domain.deep_search.model.value.FinalReport;
import com.sirmem.domain.deep_search.model.value.PlanningContext;
import com.sirmem.domain.deep_search.model.value.RawResearchNote;
import com.sirmem.domain.deep_search.model.value.ResearchBrief;
import com.sirmem.domain.deep_search.model.value.ResearchNote;
import com.sirmem.domain.deep_search.model.value.ResearchQuestion;
import com.sirmem.domain.deep_search.model.value.ResearchTopic;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResearchWorkflowStateTest {

    @Test
    void accumulatesMainWorkflowStateWithoutLettingCommandsOwnResearchOutputs() {
        ResearchQuestion question = new ResearchQuestion("What is Deep Search?");
        ClarifyWithUser clarification = ClarifyWithUser.verified("Question is specific enough.");
        ResearchBriefOutput briefOutput = ResearchBriefOutput.builder()
                .researchBrief(new ResearchBrief("Research brief"))
                .build();
        PlanningContext planningContext = new PlanningContext("Planning context");
        ResearchTopic topic = new ResearchTopic("Topic");
        ConductResearch command = ConductResearch.builder()
                .researchTopic(topic)
                .build();
        ResearcherOutputState researcherOutput = new ResearcherOutputState(
                new ResearchNote("Compressed research"),
                List.of(new RawResearchNote("Raw note"))
        );
        ResearchComplete completionSignal = ResearchComplete.signal();
        FinalReport finalReport = new FinalReport("Final report");

        ResearchWorkflowState initial = ResearchWorkflowState.from(question);
        ResearchWorkflowState clarified = initial.recordClarification(clarification);
        ResearchWorkflowState briefed = clarified.applyResearchBrief(briefOutput);
        ResearchWorkflowState planned = briefed.plan(planningContext, List.of(topic));
        ResearchWorkflowState delegated = planned.delegate(command);
        ResearchWorkflowState accumulated = delegated.accumulateResearch(researcherOutput);
        ResearchWorkflowState completeSignaled = accumulated.signalResearchComplete(completionSignal);
        ResearchWorkflowState reported = completeSignaled.writeFinalReport(finalReport);

        assertThat(initial.stage()).isEqualTo(ResearchWorkflowStage.QUESTION_RECEIVED);
        assertThat(initial.requestInput()).isEqualTo(question);
        assertThat(initial.clarificationDecision()).isEmpty();
        assertThat(initial.researchBrief()).isEmpty();
        assertThat(initial.planningContext()).isEmpty();
        assertThat(initial.researchTopics()).isEmpty();
        assertThat(initial.processedNotes()).isEmpty();
        assertThat(initial.rawNotes()).isEmpty();
        assertThat(initial.researchCompleteSignal()).isEmpty();
        assertThat(initial.finalReportOutput()).isEmpty();

        assertThat(clarified.stage()).isEqualTo(ResearchWorkflowStage.CLARIFICATION_VERIFIED);
        assertThat(clarified.clarificationDecision()).contains(clarification);
        assertThat(clarified.researchBrief()).isEmpty();

        assertThat(briefed.stage()).isEqualTo(ResearchWorkflowStage.BRIEF_GENERATED);
        assertThat(briefed.researchBrief()).contains(briefOutput.getResearchBrief());

        assertThat(planned.stage()).isEqualTo(ResearchWorkflowStage.RESEARCH_PLANNED);
        assertThat(planned.planningContext()).contains(planningContext);
        assertThat(planned.researchTopics()).containsExactly(topic);
        assertThat(planned.processedNotes()).isEmpty();
        assertThat(planned.rawNotes()).isEmpty();

        assertThat(command.getResearchTopic()).isEqualTo(topic);
        assertThat(delegated.stage()).isEqualTo(ResearchWorkflowStage.RESEARCH_IN_PROGRESS);
        assertThat(delegated.researchTopics()).containsExactly(topic);
        assertThat(delegated.processedNotes()).isEmpty();
        assertThat(delegated.rawNotes()).isEmpty();

        assertThat(accumulated.stage()).isEqualTo(ResearchWorkflowStage.RESEARCH_ACCUMULATED);
        assertThat(accumulated.processedNotes()).containsExactly(new ResearchNote("Compressed research"));
        assertThat(accumulated.rawNotes()).containsExactly(new RawResearchNote("Raw note"));

        assertThat(completeSignaled.stage()).isEqualTo(ResearchWorkflowStage.RESEARCH_COMPLETE_SIGNALED);
        assertThat(completeSignaled.researchCompleteSignal()).contains(completionSignal);
        assertThat(completeSignaled.finalReportOutput()).isEmpty();

        assertThat(reported.stage()).isEqualTo(ResearchWorkflowStage.FINAL_REPORT_GENERATED);
        assertThat(reported.finalReportOutput()).contains(finalReport);
    }

    @Test
    void clarifyWithUserRepresentsNeedClarificationQuestionAndVerification() {
        ClarifyWithUser needsClarification = ClarifyWithUser.needsClarification("What scope should I research?");
        ClarifyWithUser verified = ClarifyWithUser.verified("I have enough information.");

        assertThat(needsClarification.isNeedClarification()).isTrue();
        assertThat(needsClarification.getQuestion()).contains("What scope should I research?");
        assertThat(needsClarification.getVerification()).isEmpty();

        assertThat(verified.isNeedClarification()).isFalse();
        assertThat(verified.getQuestion()).isEmpty();
        assertThat(verified.getVerification()).contains("I have enough information.");

        assertThatThrownBy(() -> ClarifyWithUser.needsClarification(" "))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ClarifyWithUser.verified(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void exposesImmutableAccumulatedCollections() {
        ResearchTopic topic = new ResearchTopic("Topic");
        ResearchWorkflowState state = ResearchWorkflowState.from(new ResearchQuestion("Question"))
                .plan(new PlanningContext("Planning context"), List.of(topic))
                .accumulateResearch(new ResearcherOutputState(
                        new ResearchNote("Compressed research"),
                        List.of(new RawResearchNote("Raw note"))
                ));

        assertThatThrownBy(() -> state.researchTopics().add(new ResearchTopic("Other topic")))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> state.processedNotes().add(new ResearchNote("Other note")))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> state.rawNotes().add(new RawResearchNote("Other raw note")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void representsSupervisorAndResearcherStateBoundaries() {
        ResearchBrief brief = new ResearchBrief("Research brief");
        ResearchTopic topic = new ResearchTopic("Topic");
        ConductResearch command = ConductResearch.builder()
                .researchTopic(topic)
                .build();

        ResearcherWorkflowState researcherState = ResearcherWorkflowState.from(command)
                .nextToolCallIteration()
                .addRawNote(new RawResearchNote("Raw note"))
                .compress(Summary.builder()
                        .compressedResearch(new ResearchNote("Compressed research"))
                        .build());
        ResearcherOutputState researcherOutput = researcherState.toOutput();
        SupervisorWorkflowState supervisorState = SupervisorWorkflowState.from(brief)
                .nextIteration()
                .addResearcherOutput(researcherOutput);

        assertThat(supervisorState.researchBrief()).contains(brief);
        assertThat(supervisorState.processedNotes()).containsExactly(new ResearchNote("Compressed research"));
        assertThat(supervisorState.rawNotes()).containsExactly(new RawResearchNote("Raw note"));
        assertThat(supervisorState.researchIterationCount()).isEqualTo(1);

        assertThat(researcherState.researchTopic()).isEqualTo(topic);
        assertThat(researcherState.rawNotes()).containsExactly(new RawResearchNote("Raw note"));
        assertThat(researcherState.compressedResearch()).contains(new ResearchNote("Compressed research"));
        assertThat(researcherState.toolCallIterationCount()).isEqualTo(1);

        assertThat(researcherOutput.compressedResearch()).isEqualTo(new ResearchNote("Compressed research"));
        assertThat(researcherOutput.rawNotes()).containsExactly(new RawResearchNote("Raw note"));

        assertThatThrownBy(() -> supervisorState.processedNotes().add(new ResearchNote("Other note")))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> researcherState.rawNotes().add(new RawResearchNote("Other raw note")))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> researcherOutput.rawNotes().add(new RawResearchNote("Other raw note")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void researcherOutputRequiresCompressedResearch() {
        ResearcherWorkflowState researcherState = ResearcherWorkflowState.from(ConductResearch.builder()
                .researchTopic(new ResearchTopic("Topic"))
                .build());

        assertThat(researcherState.compressedResearch()).isEqualTo(Optional.empty());
        assertThatThrownBy(researcherState::toOutput)
                .isInstanceOf(IllegalStateException.class);
    }
}
