package com.sirmem.domain.deep_search.service;


import com.sirmem.types.enumeration.ResearchStatus;
import com.sirmem.types.dto.response.ResearchResponse;
import org.springframework.stereotype.Service;

@Service
public class DeterministicResearchService implements ResearchService {

    @Override
    public ResearchResponse research(com.sirmem.types.dto.request.ResearchRequest researchQuestion) {
        String finalReport = "# Research Report\n\n"
                + "Question: " + researchQuestion.question() + "\n\n"
                + "This is a deterministic stub research report.";

        return new ResearchResponse(ResearchStatus.COMPLETED, finalReport);
    }
}
