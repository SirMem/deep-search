package com.sirmem.domain.deep_search.service;

import com.sirmem.types.dto.response.ResearchResponse;
import com.sirmem.types.dto.request.ResearchRequest;

public interface ResearchService {

    ResearchResponse research(ResearchRequest researchQuestion);
}
