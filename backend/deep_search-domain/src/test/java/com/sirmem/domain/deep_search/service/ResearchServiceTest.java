package com.sirmem.domain.deep_search.service;

import com.sirmem.types.dto.request.ResearchRequest;
import com.sirmem.types.dto.response.ResearchResponse;
import com.sirmem.types.enumeration.ResearchStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResearchServiceTest {

    @Test
    void returnsCompletedResearchResponseFromWorkflowState() {
        ResearchService researchService = new DeterministicResearchService();

        ResearchResponse response = researchService.research(new ResearchRequest("What is Deep Search?"));

        assertThat(response.status()).isEqualTo(ResearchStatus.COMPLETED);
        assertThat(response.finalReport()).contains("What is Deep Search?");
        assertThat(response.finalReport()).contains("Brief: Research brief for: What is Deep Search?");
        assertThat(response.finalReport()).contains("Topic: Understand: What is Deep Search?");
        assertThat(response.finalReport()).contains("Note: Structured note for: What is Deep Search?");
        assertThat(response.finalReport()).contains("deterministic workflow state research report");
    }
}
